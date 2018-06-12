import Controller from '../common/Controller';

import handle from '../../../utils/handler';

import { FEEDBACK_SENTIMENTS } from '../../../constants';

import { goToTarget, easeIn, easeOut } from '../common';

// TODO: this is legacy and needs to be rewritten when the audio design is finalized

const SENTIMENTS = FEEDBACK_SENTIMENTS;

const ENERGY_LEVELS = 3;

const MUTE_PERCENTAGE = 25;
const ENERGY_2_PERCENTAGE = 50;
const ENERGY_3_PERCENTAGE = 75;


class VolumeObject {
  constructor(audioElement, volumeController) {
    this.audio = audioElement;
    this.volumeController = volumeController;
  }

  get volume() { return this.audio.volume; }
  set volume(value) {
    this.audio.volume = this.volumeController.volume * (value / 1);
  }
}


class VolumeController {
  constructor() {
    this.volumeObjects = [];
    this.volume = 0;
  }

  push = (audio) => {
    const volumeObject = new VolumeObject(audio, this);
    this.volumeObjects.push(volumeObject);
    return volumeObject;
  }

  /* eslint-disable no-underscore-dangle */
  get volume() { return this._volume; }
  set volume(value) {
    this._volume = value;
    this.volumeObjects.forEach((v) => {
      // reset the volume based on the new global volume
      v.volume = v.volume;
    });
  }
  /* eslint-enable */
}


class CrowdAudio extends Controller {
  // one for each tier
  volumeControllers = [
    new VolumeController(),
    new VolumeController(),
    new VolumeController(),
    new VolumeController(),
  ]
  activityLevels = {};

  lastTier = -1;
  tier = -1;

  targetActivity = 0;
  activity = 0;
  volume = 0;

  feedback = [];

  audio = [
    // tier 1
    {
      // sentiments
      // - [energy levels]
    },
    // tier 2
    {
      // sentiments
      // - [energy levels]
    },
    // tier 3
    {
      // sentiments
      // - [energy levels]
    },
    // tier 4
    {
      // sentiments
      // - [energy levels]
    },
  ];

  constructor(props) {
    super(props);

    // returns a volume control function via promise resolution
    this.getAudioSrc = (tier, sentiment, energy) => (
      this.props.assets.load({
        remote: `audio/crowd/${
          tier
        }.${
          energy
        }.${
          sentiment
        }.ogg`,
      })
        .then((audio) => {
          audio.volume = 0;
          audio.loop = true;
          audio.play();
          const volumeObject = this.volumeControllers[tier].push(audio);
          return { volumeObject, sentiment };
        })
    );
  }

  onActivityBySentimentChange = ({ activityBySentiment }) => {
    this.feedback = [];
    const keys = Object.keys(activityBySentiment);
    keys.forEach((key, i) => {
      this.feedback[i] = { id: key, activity: activityBySentiment[key] * 100 };
    });
  }

  onStageSizeChange = ({ stageSize }) => {
    const tier = Math.min(Math.floor(stageSize * 4), 3);
    if (tier !== this.tier) {
      const promises = [];
      const n = SENTIMENTS.length * 3;
      for (let i = 0; i < n; i++) {
        promises.push(this.getAudioSrc(
          tier,
          SENTIMENTS[Math.floor(i / ENERGY_LEVELS)],
          i % ENERGY_LEVELS,
        ));
      }

      Promise.all(promises)
        .then((audioObjects) => {
          for (let i = 0; i < audioObjects.length; i++) {
            if (!this.audio[tier][audioObjects[i].sentiment]) {
              this.audio[tier][audioObjects[i].sentiment] = [];
            }
            this.audio[tier][audioObjects[i].sentiment][i % 3] = audioObjects[i].volumeObject;
          }
          this.lastTier = this.tier;
          this.tier = tier;
          if (this.lastTier === -1) this.lastTier = tier;
        })
        .catch(handle.error);
    }
  }

  render = ({ timediff }) => {
    if (this.tier !== -1 && this.lastTier !== -1) {
      // fade between tiers and set lastTier to -1 when it is faded out completely
      this.volumeControllers[this.lastTier].volume =
        goToTarget(this.volumeControllers[this.lastTier].volume, 0, timediff, 0.2);
      this.volumeControllers[this.tier].volume =
        goToTarget(this.volumeControllers[this.tier].volume, 1, timediff, 0.2);
      if (this.volumeControllers[this.lastTier].volume === 0) this.lastTier = -1;
    }

    for (let i = 0; i < this.feedback.length; i++) {
      const { id } = this.feedback[i];
      const targetActivity = this.feedback[i].activity;
      if (typeof this.activityLevels[id] === 'undefined') {
        this.activityLevels[id] = targetActivity;
      }
      this.activityLevels[id] =
        goToTarget(this.activityLevels[id], targetActivity, timediff, 0.2);

      const activity = this.activityLevels[id];

      const modActivity = activity - MUTE_PERCENTAGE - 1;
      const volume = easeIn
        .getPosition((modActivity < 0 ? 0 : modActivity) / (100 - MUTE_PERCENTAGE));

      if (this.tier !== -1) {
        if (activity <= ENERGY_2_PERCENTAGE) {
          this.audio[this.tier][id][0].volume = volume;
          this.audio[this.tier][id][1].volume = 0;
          this.audio[this.tier][id][2].volume = 0;
        } else if (activity <= ENERGY_3_PERCENTAGE) {
          this.audio[this.tier][id][0].volume = volume * (
            1 - easeOut.getPosition(((activity - ENERGY_2_PERCENTAGE) / 25), false)
          );
          this.audio[this.tier][id][1].volume = volume - this.audio[this.tier][id][0].volume;
          this.audio[this.tier][id][2].volume = 0;
        } else {
          this.audio[this.tier][id][0].volume = 0;
          this.audio[this.tier][id][1].volume = volume * (
            1 - easeOut.getPosition(((activity - ENERGY_3_PERCENTAGE) / 25), false)
          );
          this.audio[this.tier][id][2].volume = volume - this.audio[this.tier][id][1].volume;
        }
      }
    }
  }
}


export default CrowdAudio;
