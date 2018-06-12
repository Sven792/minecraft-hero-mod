import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { EventEmitter } from 'fbemitter';

import handle from '../../../utils/handler';

import defaultProfilePicture from '../../../../client/assets/img/global/temp_avatar_128x128.jpg';

import { sumStreamViewers } from '../../../components/stream/StreamViewers';

import { getResource } from '../../../utils/common';

import {
  getRepTier,
  getRepMessageUptime,
} from '../common';

import Assets from '../common/Assets';
import crop from '../common/crop';

import {
  FRAMERATE,
  EVENT_NAMES,
  COLORS,
  REP_TIERS,
  SUPER_REP_TIER,
  ALIGNMENTS,
} from '../constants';

import {
  P,
  WIDTH,
  HEIGHT,
  HALF_WIDTH,
  BUBBLE_SPAWN_Y,
  BUBBLE_SPAWN_W,
  BUBBLE_SPAWN_H,
  GLOW_WIDTH,
} from '../constants/performer';

import Bubbles from '../controllers/Bubbles';
import CrowdAudio from '../controllers/CrowdAudio';
import Glow from '../controllers/Glow';
import Popup from '../controllers/Popup';
import Message from '../controllers/Message';


// ----------------------------------------------------------------------------

const loadAvatar = ({ from: url }) => (
  new Promise((resolve) => {
    const img = new Image();
    img.onload = () => {
      const cropped = crop(img, { with: 'hexagon' });
      resolve(cropped);
    };
    img.onerror = () => resolve(false);
    img.onabort = () => resolve(false);
    img.crossOrigin = 'Anonymous'; // prevent image data from tainting canvas
    img.src = url;
  })
);

const OFFSCREEN_FRAMERATE = 1000 / 15;

const VOTE_STATUS_NOT_RECEIVED_TIMEOUT = 3000;
const VOTE_BUBBLE_LIMIT = 300;

const MAX_RANDOM_MS = 500;

const POSITIONS = {
  bottomRight: 'bottom-right',
  bottomCenter: 'bottom-center',
  bottomLeft: 'bottom-left',
};

// feedback:       true/false => whether feedback based events are enabled
// feedback-audio: true/false => whether feedback audio is enabled
// viewcount:      true/false => whether viewcount is enabled
// vote:           true/false => whether vote events are displayed
// rep:            true/false => whether rep events are displayed
// rep-audio:      true/false => whether rep audio is enabled
// position:
//   one of "bottom-right", "bottom-center", or "bottom-left" =>
//     where the hud is aligned

const QUERY_PARAMS = [
  'feedback',
  'feedback-audio',
  'viewcount',
  'vote',
  'rep',
  'rep-audio',
  'position',
];
const QUERY_PARAM_NAMES = {
  feedback: 'feedback',
  'feedback-audio': 'feedbackAudio',
  viewcount: 'viewcount',
  vote: 'vote',
  rep: 'rep',
  'rep-audio': 'repAudio',
  position: 'position',
};
const QUERY_PARAM_DEFAULTS = {
  feedback: true,
  'feedback-audio': false,
  viewcount: true,
  vote: true,
  rep: true,
  'rep-audio': true,
  position: POSITIONS.bottomRight,
};

// ----------------------------------------------------------------------------


class PerformerFeedback extends Component {
  static propTypes = {
    assetHost: PropTypes.string.isRequired,
    streamClient: PropTypes.object.isRequired,
    query: PropTypes.object,

    service: PropTypes.string.isRequired,
    serviceId: PropTypes.string.isRequired,
  }

  static defaultProps = {
    query: {},
  }

  // --------------------------------------------------------------------------

  assets = new Assets({ assetHost: this.props.assetHost });

  animationFrameRequest = null;
  timeout = null;
  blurred = false;
  canvas = null;
  ctx = null;
  subscriptions = [];
  emitter = new EventEmitter();
  repEmitter = new EventEmitter();
  controllers = {};
  queries = {};

  timestamp = 0;
  lastTimestamp = 0;
  lastTime = Date.now();
  stageSize = 0;
  activity = 0;
  votes = {};
  repMessageTimeout = null;
  repQueue = [];
  repMessageOn = false;

  previousTopFeedback = false;

  voteStatusReceivedTimeout = null;

  voteId = false;
  voteTtlTimeout = null;
  voteTotal = 0;

  bubbleCrowdPercentages = {};
  bubbleTimestamps = {};

  defaultProfilePicture = getResource(this.props.assetHost)(defaultProfilePicture);


  // --------------------------------------------------------------------------

  componentWillMount() {
    for (let i = 0; i < QUERY_PARAMS.length; i++) {
      const queryId = QUERY_PARAMS[i];
      const query = this.props.query[queryId];
      if (query === '1' || query === 'true') {
        this.queries[QUERY_PARAM_NAMES[queryId]] = true;
      } else if (query === '0' || query === 'false') {
        this.queries[QUERY_PARAM_NAMES[queryId]] = false;
      } else if (/audio/.test(queryId)) {
        this.queries[QUERY_PARAM_NAMES[queryId]]
          = window.FEEDBACK_AUDIO_ENABLED ?
            QUERY_PARAM_DEFAULTS[queryId] :
            false;
      } else if (
        typeof QUERY_PARAM_DEFAULTS[queryId] !== 'boolean'
        && typeof query === 'string'
        && query.length > 0
      ) {
        // assign value to string query for non boolean query parameters
        this.queries[QUERY_PARAM_NAMES[queryId]] = query;
      } else {
        this.queries[QUERY_PARAM_NAMES[queryId]] =
          QUERY_PARAM_DEFAULTS[queryId];
      }
    }
  }

  componentDidMount() {
    this.ctx = this.canvas.getContext('2d');
    const props = {
      ctx: this.ctx,
      emitter: this.emitter,
      assets: this.assets,
    };

    let messageProps = props;
    let glowProps = props;
    let bubbleProps = props;
    let popupProps = props;

    // TODO optimize glow to only render half when on sides
    switch (this.queries.position) {
      case POSITIONS.bottomRight:
        break;
      case POSITIONS.bottomCenter:
        messageProps = {
          ...props,
          position: HALF_WIDTH,
          alignment: ALIGNMENTS.CENTER,
        };
        glowProps = {
          ...props,
          size: GLOW_WIDTH,
          center: HALF_WIDTH,
          alignment: ALIGNMENTS.CENTER,
        };
        bubbleProps = {
          ...props,
          alignment: ALIGNMENTS.CENTER,
          spawnBox: {
            x: 20 * P,
            y: BUBBLE_SPAWN_Y,
            w: BUBBLE_SPAWN_W,
            h: BUBBLE_SPAWN_H,
          },
        };
        popupProps = {
          ...props,
          popupAreaMinimum: 3,
          alignment: ALIGNMENTS.CENTER,
        };
        break;
      case POSITIONS.bottomLeft:
        messageProps = {
          ...props,
          position: 5 * P,
          alignment: ALIGNMENTS.LEFT,
        };
        glowProps = {
          ...props,
          center: 0,
          alignment: ALIGNMENTS.LEFT,
        };
        bubbleProps = {
          ...props,
          alignment: ALIGNMENTS.LEFT,
          spawnBox: {
            x: 5 * P,
            y: BUBBLE_SPAWN_Y,
            w: BUBBLE_SPAWN_W,
            h: BUBBLE_SPAWN_H,
          },
        };
        popupProps = {
          ...props,
          alignment: ALIGNMENTS.LEFT,
        };
        break;
      default:
        break;
    }


    this.controllers = {
      message: new Message(messageProps),
      glow: new Glow(glowProps),
      bubbles: new Bubbles(bubbleProps),
      popup: new Popup(popupProps),
    };

    if (this.queries.rep) {
      this.controllers.repMessage = new Message({
        ...messageProps,
        emitter: this.repEmitter,
        defaultFont: '$(pixel-size)px ministry, sans-serif',
        defaultColor: COLORS.hex.white,
        defaultSize: 5 * P,
        y: 37 * P,
      });
      this.controllers.repPopup = new Popup({
        ...popupProps,
        emitter: this.repEmitter,
        popupAreaMaximum: 19,
        popupAreaMinimum: 3,
      });
    }

    if (this.queries.feedbackAudio) {
      this.controllers.crowdAudio = new CrowdAudio(props);
    }

    this.startTime = Date.now();
    this.requestAnimationFrame();

    this.subscriptions.push(
      this.props.streamClient.sub('viewers', this.onViewers),
      ...(!this.queries.feedback ? [] :
        // [this.props.streamClient.sub('feedback-activity', this.onFeedbackActivity)]
        [this.props.streamClient.sub('feedback-top', this.onTopFeedback)]
      ),
      ...(!this.queries.vote ? [] :
        [
          this.props.streamClient.sub('vote', this.onVote),
          this.props.streamClient.sub('vote-status', this.onVoteStatus),
        ]
      ),
      ...(!this.queries.rep ? [] :
        [this.props.streamClient.sub('rep', this.onRep)]
      ),
    );

    window.addEventListener('visibilitychange', this.handleVisibilityChange);

    setTimeout(() => {
      this.emitter.emit(EVENT_NAMES.popup, { id: 'hero' });
    }, 1000);
  }

  componentWillUnmount() {
    if (this.animationFrameRequest) {
      window.cancelAnimationFrame(this.animationFrameRequest);
      this.animationFrameRequest = null;
    }
    if (this.timeout) {
      clearTimeout(this.timeout);
      this.timeout = null;
    }
    this.blurred = false;
    this.canvas = null;
    this.ctx = null;
    this.subscriptions.forEach(v => v.remove());
    this.subscriptions = [];
    this.emitter.removeAllListeners();
    this.controllers = {};
    this.queries = {};

    this.timestamp = 0;
    this.lastTimestamp = 0;
    this.lastTime = Date.now();
    this.stageSize = 0;
    this.activity = 0;
    this.votes = {};

    if (this.repMessageTimeout) {
      clearTimeout(this.repMessageTimeout);
      this.repMessageTimeout = null;
    }

    this.repQueue = [];
    this.repMessageOn = false;

    if (this.voteStatusReceivedTimeout) {
      clearTimeout(this.voteStatusReceivedTimeout);
      this.voteStatusReceivedTimeout = null;
    }

    this.voteId = false;
    this.voteTtlTimeout = null;
    this.voteTotal = 0;

    this.bubbleCrowdPercentages = {};
    this.bubbleTimestamps = {};

    window.removeEventListener(
      'visibilitychange',
      this.handleVisibilityChange,
    );
  }

  // --------------------------------------------------------------------------

  handleVisibilityChange = () => {
    this.blurred = document.hidden;
    this.requestAnimationFrame();
  }

  // --------------------------------------------------------------------------

  requestAnimationFrame = () => {
    if (!this.ctx) return;
    if (this.blurred) {
      if (this.animationFrameRequest) {
        window.cancelAnimationFrame(this.animationFrameRequest);
        this.animationFrameRequest = null;
      }
      this.timeout = setTimeout(() => {
        this.timestamp += (Date.now() - this.lastTime);
        this.renderCanvas(this.timestamp);
      }, OFFSCREEN_FRAMERATE);
    } else {
      if (this.timeout) {
        clearTimeout(this.timeout);
        this.timeout = null;
      }
      this.animationFrameRequest =
        window.requestAnimationFrame(this.renderCanvas);
    }
  }

  // --------------------------------------------------------------------------

  // (for hooking up to new feedback event type)
  // onFeedbackActivity = ({ top, total, options }) => {
  //   const topFeedback = {
  //     id: top,
  //     activity: options[top],
  //   };

  //   if (!this.previousTopFeedback) {
  //     this.previousTopFeedback = topFeedback.id;
  //   } else if (this.previousTopFeedback !== topFeedback.id) {
  //     this.emitter.emit(EVENT_NAMES.popup, topFeedback);
  //     this.previousTopFeedback = topFeedback.id;
  //   }

  //   const sentiments = Object.keys(options);
  //   let totalActivity = 0;
  //   for (let i = 0; i < sentiments.length; i++) {
  //     totalActivity += options[sentiments[i]];
  //   }

  //   this.bubbleCrowdPercentages = {};
  //   for (let i = 0; i < sentiments.length; i++) {
  //     this.bubbleCrowdPercentages[sentiments[i]] =
  //       options[sentiments[i]] / totalActivity;
  //   }

  //   this.activity = total;

  //   this.emitter.emit(
  //     EVENT_NAMES.limitedActivity,
  //     { activity: Math.min(total, this.stageSize) },
  //   );
  //   this.emitter.emit(EVENT_NAMES.activity, { total });
  // }

  onTopFeedback = ({ options }) => {
    const topFeedback = options[0];
    if (!this.previousTopFeedback) {
      this.previousTopFeedback = topFeedback.id;
    } else if (this.previousTopFeedback !== topFeedback.id) {
      this.emitter.emit(EVENT_NAMES.popup, {
        id: topFeedback.id,
        activity: topFeedback.activity / 100,
      });
      this.previousTopFeedback = topFeedback.id;
    }
    let totalActivity = Math.ceil((
      (options[0].activity * 0.8) +
      (options[1].activity * 0.1) +
      (options[2].activity * 0.1)
    ));
    totalActivity = totalActivity > 100 ? 100 : totalActivity;

    const summedActivity = options.reduce((r, v) => r + v.activity, 0);

    const activityBySentiment = {
      [options[0].id]: options[0].activity / summedActivity || 0,
      [options[1].id]: options[1].activity / summedActivity || 0,
      [options[2].id]: options[2].activity / summedActivity || 0,
    };

    this.bubbleCrowdPercentages = {
      ...activityBySentiment,
    };

    const activity = totalActivity / 100;

    this.activity = activity;
    console.warn(`Activity: ${totalActivity}%`);

    this.emitter.emit(
      EVENT_NAMES.activityBySentiment,
      { activityBySentiment },
    );

    this.emitter.emit(
      EVENT_NAMES.limitedActivity,
      { activity: Math.min(activity, this.stageSize) },
    );
    this.emitter.emit(EVENT_NAMES.activity, { activity });
  }

  onViewers = (data) => {
    const viewcount = sumStreamViewers(data);
    this.viewcount = viewcount;
    if (this.queries.viewcount) {
      this.emitter.emit(EVENT_NAMES.viewcount, { viewcount });
      this.sendViewcountMessage();
    }
    const stageSize = Math.min(Math.log(viewcount + 1), 10) / 10;
    this.stageSize = stageSize;
    this.emitter.emit(EVENT_NAMES.stageSize, { stageSize });
    console.warn(`Stage size: ${Math.floor(stageSize * 100)}%`);
  }

  onVote = (vote) => {
    if (this.props.service && this.props.serviceId) {
      vote.local = {};
      vote.local.startTime = (
        this.timestamp - (Date.now() - Date.parse(vote['start-ts']))
      );
      vote.local.endTime = (
        this.timestamp - (Date.now() - Date.parse(vote['close-ts']))
      );
      this.votes[vote.id] = vote;
      this.voteStatusReceivedTimeout = setTimeout(() => {
        this.voteStatusReceivedTimeout = null;
        if (this.votes[vote.id] && !this.votes[vote.id].local.statusReceived) {
          delete this.votes[vote.id];
        }
      }, VOTE_STATUS_NOT_RECEIVED_TIMEOUT);
    }
  }

  onVoteStatus = (status) => {
    if (this.props.service && this.props.serviceId) {
      const voteId = status['vote-id'];
      const vote = this.votes[voteId];
      const isVote = !!vote;

      if (isVote) {
        let bubbleAmount = status.total - this.total;
        if (bubbleAmount > VOTE_BUBBLE_LIMIT) bubbleAmount = VOTE_BUBBLE_LIMIT;
        const wait = 1000 / bubbleAmount;
        for (let i = 0; i < bubbleAmount; i++) {
          setTimeout(() => {
            this.emitter.emit(EVENT_NAMES.bubble, { id: 'vote' });
          }, wait * i);
        }
        this.total = status.total;

        vote.local.statusReceived = true;
        if (this.voteTtlTimeout) {
          clearTimeout(this.voteTtlTimeout);
          this.voteTtlTimeout = null;
        }
        if (status.final) {
          this.onVoteEnd(vote, voteId);
        } else {
          if (!vote.local.started) {
            vote.local.started = true;
            this.emitter.emit(EVENT_NAMES.popup, {
              id: 'vote',
              inOut: true,
              vote,
            });
          }
          const msTtl = 1000 * status.ttl;
          // handle cleanup of vote data on network failure
          this.voteTtlTimeout = setTimeout(() => {
            this.onVoteEnd(vote, voteId);
          }, msTtl + 3000);
          this.voteId = voteId;
          vote.local.endTime = this.timestamp + msTtl;
          this.emitter.emit(EVENT_NAMES.message, {
            text: `hero.tv/${this.props.service}/${this.props.serviceId}`,
            color: COLORS.hex.white,
            size: 5 * P,
            override: true,
            type: EVENT_NAMES.vote,
          });
        }
      }
    }
  }

  onVoteEnd = (vote, voteId) => {
    vote.local.final = true;
    this.voteId = false;
    this.voteTotal = 0;
    if (this.voteTtlTimeout) clearTimeout(this.voteTtlTimeout);
    this.voteTtlTimeout = null;
    if (this.votes[voteId]) delete this.votes[voteId];
    // wipe the override and remove the message
    this.emitter.emit(EVENT_NAMES.message, { remove: true, override: true });
    // send viewcount message if viewcount is enabled
    this.sendViewcountMessage();
  }

  onRep = ({ amount, sender }) => {
    const tier = getRepTier(amount);

    if (this.queries.repAudio && tier > 1) {
      this.assets.load({ remote: `audio/rep/${tier}.ogg` })
        .then((audio) => {
          audio.play();
          this.emitRep({ amount, sender, tier });
        })
        .catch((err) => {
          handle.error(err);
          // still play animation if audio fails to load
          this.emitRep({ amount, sender, tier });
        });
    } else {
      this.emitRep({ amount, sender, tier });
    }
  }

  // --------------------------------------------------------------------------

  emitRep = ({ amount, sender, tier }) => {
    if (tier === REP_TIERS.length - 1) {
      Promise.all([
        loadAvatar({
          from: sender.avatar || this.defaultProfilePicture,
        }),
        this.assets.load({ local: `rep-${tier}` }),
      ])
        .then(([avatar, img]) => {
          this.pushRepMessage({
            avatar,
            amount,
            sender,
            tier,
            img,
          });
        });
    } else {
      // emit standard rep
      this.assets.load({ local: `rep-${tier}` })
        .then((img) => {
          this.pushRepMessage({
            amount,
            sender,
            tier,
            img,
          });
        });
    }

    this.repEmitter.emit(EVENT_NAMES.popup, {
      id: `rep.small.${tier}`,
      activity: (
        ((amount / (REP_TIERS[tier + 1] || SUPER_REP_TIER)) * 0.5)
        + 0.5
      ),
    });
  }

  pushRepMessage = (repMessage) => {
    if (this.repMessageOn || this.repQueue.length > 0) {
      this.repQueue.push(repMessage);
    } else {
      this.sendRepMessage(repMessage);
    }
  }

  popRepMessage = () => {
    if (this.repQueue.length > 0) {
      this.repEmitter.emit(EVENT_NAMES.message, {
        remove: true,
      });
      this.sendRepMessage(this.repQueue.shift());
    } else {
      this.repEmitter.emit(EVENT_NAMES.message, {
        remove: true,
      });
      this.repMessageOn = false;
      this.sendViewcountMessage();
    }
  }

  sendRepMessage = ({
    avatar,
    amount,
    sender,
    tier,
    img,
  }) => {
    this.repMessageOn = true;
    const uptime = getRepMessageUptime({ amount });
    const uname = (sender.uname || 'UNKNOWN').toUpperCase();
    const parsedAmount = (amount || 0).toLocaleString();
    this.repEmitter.emit(EVENT_NAMES.message, {
      text: `${uname} REPPED ${parsedAmount}`,
      rep: {
        img,
        textArray: [
          uname,
          ' REPPED ',
          parsedAmount,
        ],
        tier,
      },
    });
    if (avatar) {
      this.emitter.emit(EVENT_NAMES.popup, {
        id: `rep.large.${tier}`,
        inOut: true,
        rep: {
          uname,
          avatar,
          endTime: this.timestamp + uptime,
        },
      });
    }
    this.repMessageTimeout = setTimeout(this.popRepMessage, uptime);
  }

  // --------------------------------------------------------------------------

  sendViewcountMessage = () => {
    if (this.queries.viewcount && this.viewcount !== 0) {
      this.assets.load({ local: 'viewcount' })
        .then((img) => {
          this.emitter.emit(EVENT_NAMES.message, {
            text: this.viewcount,
            type: EVENT_NAMES.viewcount,
            viewcount: {
              img,
            },
          });
        });
    } else {
      this.emitter.emit(EVENT_NAMES.message, { remove: true });
    }
  }

  // --------------------------------------------------------------------------

  spawnBubbles = (timestamp) => {
    const types = Object.keys(this.bubbleCrowdPercentages);
    for (let i = 0; i < types.length; i++) {
      const type = types[i];
      const percentage = this.bubbleCrowdPercentages[type];
      if (typeof this.bubbleTimestamps[type] === 'undefined') {
        // prevent timings lining up on load
        this.bubbleTimestamps[type] = timestamp + (Math.random() * MAX_RANDOM_MS);
      }
      const startTime = this.bubbleTimestamps[type];

      const timediff = timestamp - startTime;

      const waitTime = (
        16
        + (
          500
          * (1 / (this.activity * 3))
          * (1 / (Math.max(this.stageSize, 0.1) * 5))
          * (1 / (percentage * 5))
          * (0.6 + (Math.random() * 0.8))
        )
      );

      if (timediff >= waitTime && percentage !== 0 && this.activity !== 0) {
        this.emitter.emit(EVENT_NAMES.bubble, { id: type });

        this.bubbleTimestamps[type] = timestamp;
      } else if (percentage === 0 || this.activity === 0) {
        this.bubbleTimestamps[type] = timestamp + (Math.random() * MAX_RANDOM_MS);
      }
    }
  }

  // --------------------------------------------------------------------------

  renderCanvas = (timestamp) => {
    if (!this.ctx) return;
    const timediff = timestamp - this.lastTimestamp;
    if (timediff < FRAMERATE) {
      this.requestAnimationFrame();
      return;
    }
    this.timestamp = timestamp;
    this.spawnBubbles(timestamp);
    this.lastTimestamp = timestamp;
    this.lastTime = Date.now();
    this.ctx.clearRect(0, 0, WIDTH, HEIGHT);
    const args = { timestamp, timediff };

    this.controllers.glow.render(args);
    this.controllers.bubbles.render(args);
    this.controllers.message.render(args);
    this.controllers.popup.render(args);

    if (this.queries.rep) {
      this.controllers.repMessage.render(args);
      this.controllers.repPopup.render(args);
    }

    if (this.queries.feedbackAudio) {
      this.controllers.crowdAudio.render(args);
    }

    this.requestAnimationFrame();
  }

  // --------------------------------------------------------------------------

  render = () => (
    <div className="performer-feedback feedback-geary">
      <canvas
        ref={(c) => { this.canvas = c; }}
        className="performer-feedback-canvas"
        width={WIDTH}
        height={HEIGHT}
      />
    </div>
  )
}


export default PerformerFeedback;
