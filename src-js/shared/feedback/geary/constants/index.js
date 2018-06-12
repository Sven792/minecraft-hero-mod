// the framerate needs a fudge factor so the requestAnimationFrame calls
//   triggered by timeouts can reliably time frames
export const FRAMERATE = 1000 / 35;

export const EVENT_NAMES = {
  activity: 'activity',
  limitedActivity: 'limited-activity',
  viewcount: 'viewcount',
  stageSize: 'stage-size',
  topFeedback: 'top-feedback',
  popup: 'popup',
  bubble: 'bubble',
  message: 'message',
  vote: 'vote',
  activityBySentiment: 'activity-by-sentiment',
};

export const LISTENER_NAMES = {
  activity: 'onActivityChange',
  limitedActivity: 'onLimitedActivityChange',
  viewcount: 'onViewcountChange',
  stageSize: 'onStageSizeChange',
  topFeedback: 'onTopFeedback',
  popup: 'onPopup',
  bubble: 'onBubble',
  message: 'onMessage',
  vote: 'onVote',
  activityBySentiment: 'onActivityBySentimentChange',
};

export const COLORS = {
  hex: {
    white: '#F4F1EF',
    pink: '#E51345',
    yellow: '#FF9B12',
  },
  rgb: {
    pink: '259,19,49',
  },
  num: {
    blue: [24, 241, 255],
    orange: [255, 108, 10],
  },
};

export const REP_TIERS = [
  1,
  10,
  25,
  100,
  1000,
];

export const SUPER_REP_TIER = 100000;


export const ALIGNMENTS = {
  CENTER: 'center',
  RIGHT: 'right',
  LEFT: 'left',
};

export const ALIGNMENTS_LIST = (
  Object.keys(ALIGNMENTS)
    .map(key => ALIGNMENTS[key])
);
