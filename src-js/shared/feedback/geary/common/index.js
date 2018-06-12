import { REP_TIERS } from '../constants';

export const getFrameIndex = (
  timestamp,
  animationStart = 0,
  framerate = 30,
) => (
  Math.floor(((timestamp - animationStart) / framerate))
);


export const getAnimPos = (
  size,
  sourceSize,
  spriteSourceSize,
  basePixelSize,
) => ({
  x: size * (spriteSourceSize.x / basePixelSize),
  y: size * (spriteSourceSize.y / basePixelSize),
  w: size * (spriteSourceSize.w / basePixelSize),
  h: size * (spriteSourceSize.h / basePixelSize),
  source: {
    w: size * (sourceSize.w / basePixelSize),
    h: size * (sourceSize.h / basePixelSize),
  },
});


export const goToTarget = (value, target, timeDiff, speed = 1) => {
  let newValue = value;
  if (value !== target) {
    const up = (target > value);
    // the values here are made larger or smaller by 0.2 so as to make the
    //   value reach the target faster
    const distance = up ? (target * 1.2) - value : (target * 0.8) - value;
    newValue += ((distance / 1000) * speed) * timeDiff;
    if (up && newValue > target) newValue = target;
    else if (!up && newValue < target) newValue = target;
  }
  return newValue;
};


export class AnimationHandler {
  constructor(
    spline,
    {
      startTime = 0,
      speed = 1,
      duration = 1,
      loop = true,
      distance = 1,
      startingPosition = 0,
    },
  ) {
    this.spline = spline;
    this.startTime = startTime;
    this.duration = duration;
    this.speed = speed;
    this.loop = loop;
    this.distance = distance;
    this.startingPosition = startingPosition;
  }

  getPosition = time => (
    (
      this.spline.getPosition(
        ((time - this.startTime) / this.duration) * this.speed,
        this.loop,
      ) *
      this.distance
    )
    + this.startingPosition
  )
}


export class Spline {
  constructor(type) {
    this.data = [];
    this.type = type;
    this.length = 0;
  }

  push = (...args) => {
    this.length = (this.data.length / 4) + 1;
    this.set(...args, this.data.length / 4);
  }

  set = (A, B, C, D, index) => {
    switch (this.type) {
      case 'bezier':
        // a = p1, b = p2, c = p3, d = p4
        this.data[(index * 4)] = ((-A + (B * 3)) - (C * 3)) + D;
        this.data[(index * 4) + 1] = ((A * 3) - (B * 6)) + (C * 3);
        this.data[(index * 4) + 2] = -(A * 3) + (B * 3);
        this.data[(index * 4) + 3] = A;
        break;
      case 'hermite':
        // a = p1, b = p2, c = m1, d = m2
        this.data[(index * 4)] = ((A * 2) - (B * 2)) + C + D;
        this.data[(index * 4) + 1] = (-(A * 3) + (B * 3)) - (C * 2) - D;
        this.data[(index * 4) + 2] = C;
        this.data[(index * 4) + 3] = A;
        break;
      default:
        throw new Error('No such spline type');
    }
  }

  /**
   * Get a position on the spline for a specific time value
   * @param {number} time - a value between 0 and 1
   * @param {[boolean]} loop - whether or not to loop the spline
   * @returns - a value between 0 and 1
   */
  getPosition = (baseTime, loop = true) => {
    const time = baseTime * this.length;
    let p;
    let t = time % 1;
    if (loop && this.data.length > 0) {
      p = Math.floor(time % this.length);
    } else if (this.data.length > 0) {
      p = Math.floor(time);
      if (p >= this.length) {
        p = this.length - 1;
        t = 1;
      }
    } else {
      return -1;
    }
    return Math.max(
      0,
      Math.min(
        1,
        (
          (this.data[(p * 4)] * (t ** 3)) +
          (this.data[(p * 4) + 1] * (t ** 2)) +
          (this.data[(p * 4) + 2] * t) +
          this.data[(p * 4) + 3]
        ),
      ),
    );
  }
}

export const easeOut = new Spline('hermite');
easeOut.push(0, 1, 2, 0);

export const easeIn = new Spline('hermite');
easeIn.push(0, 1, 0, 2);

export const linear = new Spline('hermite');
linear.push(0, 1, 1, 1);


export const POP_IN_END = 0.6;
export const popIn = new Spline('hermite');
popIn.push(0, 0.5, 0, 1);
popIn.push(0.5, 0.6, 1, -0.3);
popIn.push(0.6, POP_IN_END, -0.3, 0);

export const popOut = new Spline('hermite');
popOut.push(1, 0, 0, -3);

const transition = {
  in: popIn,
  out: popOut,
};

/**
 * Creates an AnimationHandler with a pop in or out animation
 * @param {string} direction one of (in|out)
 * @param {number} timestamp the timestamp at the start of the animation
 * @param {number} duration how long the animation should last (default 400ms)
 */
export const makePopHandler = ({
  direction,
  timestamp,
  duration = 400,
  distance = 1,
  startingPosition = false,
}) => {
  if (!direction || !/(in|out)/.test(direction)) {
    throw new Error(`invalid direction "${direction}"`);
  }
  return new AnimationHandler(transition[direction], {
    startTime: timestamp,
    duration,
    loop: false,
    distance: (
      direction === 'in' ?
        1 / POP_IN_END :
        1
    ) * distance,
    ...(startingPosition !== false ? { startingPosition } : {}),
  });
};


export const getRepTier = (amount) => {
  let tier = 0;

  for (let i = REP_TIERS.length - 1; i >= 0; i--) {
    if (amount >= REP_TIERS[i]) {
      tier = i;
      break;
    }
  }

  return tier;
};


export const getRepMessageUptime = ({ amount }) => (
  Math.min(Math.log10(amount + 5), 4) * 2000
);
