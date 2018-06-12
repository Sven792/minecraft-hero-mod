import PropTypes from 'prop-types';

import handle from '../../../utils/handler';

import Controller from '../common/Controller';

import {
  getFrameIndex,
  getAnimPos,
  makePopHandler,
} from '../common';

import {
  ALIGNMENTS,
  ALIGNMENTS_LIST,
} from '../constants';

import { HALF_PI, TWO_PI } from '../constants/math';

// performer specific constants may only be used in defaultProps
import {
  P,
  WIDTH,
  HEIGHT,
  HALF_WIDTH,
} from '../constants/performer';


const VOTE_GAUGE_FILL_COLOR = '#22C1BE';
const VOTE_GAUGE_EMPTY_COLOR = '#D1CECC';
const ONE_PT_FIVE_PI = Math.PI * 1.5;


class Popup extends Controller {
  static propTypes = {
    assets: PropTypes.shape({
      load: PropTypes.func.isRequired,
    }).isRequired,

    bottom: PropTypes.number.isRequired,
    center: PropTypes.number.isRequired,

    width: PropTypes.number.isRequired,

    spawnWidth: PropTypes.number.isRequired,

    spriteBasePixels: PropTypes.number.isRequired,

    popupAreaMinimum: PropTypes.number.isRequired,
    popupAreaMaximum: PropTypes.number.isRequired,

    animationTop: PropTypes.number.isRequired,

    alignment: PropTypes.oneOf(ALIGNMENTS_LIST).isRequired,
  }

  static defaultProps = {
    bottom: HEIGHT,
    center: HALF_WIDTH,

    width: 15 * P,

    spawnWidth: WIDTH - (10 * P),

    spriteBasePixels: 100,

    popupAreaMinimum: 2,
    popupAreaMaximum: 7,

    animationTop: 25 * P,

    alignment: ALIGNMENTS.RIGHT,
  }

  timestamp = 0;
  popups = [];

  popupAreaWidth = this.props.spawnWidth / this.props.popupAreaMaximum
  popupAreaSpawnWidth = Math.max(this.popupAreaWidth - this.props.width, 0)
  popupAreas = new Array(this.props.popupAreaMaximum).fill(0);

  getFreeIndex = () => {
    const filledSpacesAmount =
      this.popupAreas.reduce((r, v) => (r + (v > 0 ? 1 : 0)), 0);
    const spacesAmount = Math.min(
      Math.max(
        this.props.popupAreaMinimum,
        filledSpacesAmount + 2,
      ),
      this.popupAreas.length,
    );
    const freeSpacesAmount = spacesAmount - filledSpacesAmount;
    let space = Math.floor(Math.random() * freeSpacesAmount);
    let index = 0;
    for (let i = 0; i < this.popupAreas.length; i++) {
      if (this.popupAreas[i] === 0) {
        if (space < 1) {
          index = i;
          break;
        }
        space--;
      }
    }
    return index;
  }

  getDisplayIndex = ({ index }) => {
    let displayIndex = 0;
    let toggle = -1;
    switch (this.props.alignment) {
      case ALIGNMENTS.CENTER:
        displayIndex = Math.floor(this.popupAreas.length / 2);
        toggle = -1;

        for (let i = 0; i < index; i++) {
          displayIndex += toggle;
          toggle = Math.sign(-toggle) * (Math.abs(toggle) + 1);
        }
        return displayIndex;
      case ALIGNMENTS.RIGHT:
        return this.popupAreas.length - 1 - index;
      case ALIGNMENTS.LEFT:
        return index;
      default:
        handle.error(new Error('Invalid alignment specified'));
        return displayIndex;
    }
  }

  onPopup = ({
    id,
    activity = 1,
    inOut = false,
    vote,
    rep,
  }) => {
    Promise.all(inOut ? [
      this.props.assets.load({ remote: `img/${id}.in.png` }),
      this.props.assets.load({ remote: `img/${id}.in.json` }),
      this.props.assets.load({ remote: `img/${id}.out.png` }),
      this.props.assets.load({ remote: `img/${id}.out.json` }),
    ] : [
      this.props.assets.load({ remote: `img/${id}.png` }),
      this.props.assets.load({ remote: `img/${id}.json` }),
    ])
      .then(([spritesheet, spriteData, outSpritesheet, outSpriteData]) => {
        let index;

        if (vote && this.popupAreas[0] === 0) {
          index = 0;
        } else {
          index = this.getFreeIndex();
        }

        this.popupAreas[index]++;

        const displayIndex = this.getDisplayIndex({ index });

        const position = (
          -(this.props.spawnWidth / 2)
          + (displayIndex * this.popupAreaWidth)
          + (this.popupAreaWidth / 2)
          + (
            (Math.random() * this.popupAreaSpawnWidth)
            - (this.popupAreaSpawnWidth / 2)
          )
        );
        this.popups.push({
          spritesheet,
          spriteData,
          ...(!inOut ? {} : {
            outSpritesheet,
            outSpriteData,
            inOut: true,
          }),
          ...(!vote ? {} : {
            vote: { ...vote },
          }),
          ...(!rep ? {} : {
            rep: { ...rep },
          }),
          startTime: this.timestamp,
          position,
          index,
          widthMod: (activity * 0.6) + 0.4,
        });
      })
      .catch(handle.error);
  }

  renderFrame = ({
    spritesheet,
    spriteData,
    frameIndex,
    widthMod,
  }) => {
    const { frame, sourceSize, spriteSourceSize } = spriteData[frameIndex];

    const pos = getAnimPos(
      this.props.width * widthMod,
      sourceSize,
      spriteSourceSize,
      this.props.spriteBasePixels,
    );

    this.props.ctx.drawImage(
      spritesheet,
      frame.x,
      frame.y,
      frame.w,
      frame.h,
      -(pos.source.w / 2) + pos.x,
      -pos.source.h + pos.y,
      pos.w,
      pos.h,
    );
  }

  transitionOut = ({ popup }) => {
    popup.spritesheet = popup.outSpritesheet;
    popup.spriteData = popup.outSpriteData;
    popup.startTime = this.timestamp;
    popup.inOut = false;
  }

  renderVote = ({
    vote,
    timestamp,
    spritesheet,
    spriteData,
    widthMod,
    popup,
  }) => {
    if (!vote.popHandler) {
      vote.popHandler = makePopHandler({ direction: 'in', timestamp });
    }

    const sizePercentage = vote.popHandler.getPosition(timestamp);

    const x = 0;
    const y = -this.props.animationTop;
    const r = ((this.props.width * widthMod) * 0.4) * sizePercentage;

    const voteProgressPercentage = Math.min(
      1 - (
        (vote.local.endTime - timestamp)
        / (vote.local.endTime - vote.local.startTime)
      ),
      1,
    );

    // fill gauge
    this.props.ctx.fillStyle = VOTE_GAUGE_FILL_COLOR;
    this.props.ctx.beginPath();
    this.props.ctx.arc(
      x, y, r,
      -HALF_PI,
      -Math.min(
        Math.PI * (-1.5 + (2 * voteProgressPercentage)),
        ONE_PT_FIVE_PI,
      ),
    );
    this.props.ctx.lineTo(x, y);
    this.props.ctx.fill();
    this.props.ctx.closePath();

    // render sprite
    this.renderFrame({
      spritesheet,
      spriteData,
      frameIndex: spriteData.length - 1,
      widthMod,
    });

    const voteOver = (vote.local.final || vote.local.endTime < timestamp);

    if (!vote.transitioningOut && voteOver) {
      vote.transitioningOut = true;
      vote.popHandler = makePopHandler({ direction: 'out', timestamp });
    }
    if (voteOver && sizePercentage < 0.3) {
      vote.popHandler = false;
      vote.transitioningOut = false;
      // play out animation
      this.transitionOut({ popup });
    }
  }

  renderRep = ({
    rep,
    spritesheet,
    spriteData,
    widthMod,
    frameIndex,
    popup,
  }) => {
    const x = 0;
    const y = -this.props.animationTop;
    const r = ((this.props.width * widthMod) * 0.5);

    // draw avatar
    this.props.ctx.drawImage(
      rep.avatar,
      x - r,
      y - r,
      r * 2,
      r * 2,
    );

    // render sprite
    this.renderFrame({
      spritesheet,
      spriteData,
      frameIndex: Math.min(frameIndex, spriteData.length - 1),
      widthMod,
    });

    const repOver = this.timestamp > rep.endTime;

    if (!rep.transitioningOut && repOver) {
      rep.transitioningOut = true;
    }
    if (repOver && !rep.transitionOut) {
      rep.transitioningOut = false;
      // play out animation
      popup.startTime = this.timestamp;
      rep.transitionOut = true;
    }
  }

  render = ({ timestamp }) => {
    this.timestamp = timestamp;

    this.props.ctx.translate(this.props.center, this.props.bottom);

    for (let i = 0; i < this.popups.length; i++) {
      const {
        spritesheet,
        spriteData,
        startTime,
        position,
        index,
        widthMod,
        inOut,
        vote,
        rep,
      } = this.popups[i];

      const frameIndex = getFrameIndex(timestamp, startTime);
      this.props.ctx.translate(position, 0);

      if (
        rep && rep.transitionedIn
      ) {
        const shouldEnd = rep.transitionOut && frameIndex >= spriteData.length;
        this.renderRep({
          rep,
          timestamp,
          spritesheet,
          spriteData,
          widthMod,
          frameIndex: rep.transitionOut ?
            Math.max(0, spriteData.length - 1 - frameIndex) :
            frameIndex,
          popup: this.popups[i],
        });
        if (shouldEnd) {
          rep.transitionedIn = false;
          this.transitionOut({ popup: this.popups[i] });
        }
      } else if (frameIndex < spriteData.length) {
        this.renderFrame({
          spritesheet,
          spriteData,
          frameIndex: rep && rep.transitionOut ?
            spriteData.length - 1 - frameIndex :
            frameIndex,
          widthMod,
        });
      } else if (!inOut) {
        this.popupAreas[index]--;
        this.popups.splice(i, 1);
        i--;
      } else if (vote) {
        this.renderVote({
          vote,
          timestamp,
          spritesheet,
          spriteData,
          widthMod,
          popup: this.popups[i],
        });
      } else if (rep) {
        rep.transitionedIn = true;
        this.renderRep({
          rep,
          timestamp,
          spritesheet,
          spriteData,
          frameIndex: spriteData.length - 1,
          widthMod,
          popup: this.popups[i],
        });
        this.transitionOut({ popup: this.popups[i] });
        this.popups[i].outSpriteData = spriteData;
        this.popups[i].outSpritesheet = spritesheet;
      } else {
        this.renderFrame({
          spritesheet,
          spriteData,
          frameIndex: spriteData.length - 1,
          widthMod,
        });

        this.transitionOut({ popup: this.popups[i] });
      }

      this.props.ctx.translate(-position, 0);
    }

    this.props.ctx.translate(-this.props.center, -this.props.bottom);
  }
}


export default Popup;
