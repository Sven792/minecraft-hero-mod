import PropTypes from 'prop-types';

import Controller from '../common/Controller';

import {
  AnimationHandler,
  Spline,
} from '../common';

import { COLORS } from '../constants';

// performer specific constants may only be used in defaultProps
import {
  P,
  WIDTH,
  HEIGHT,
  GLOW_WIDTH,
} from '../constants/performer';


const strongEaseOut = new Spline('hermite');
strongEaseOut.push(0, 1, 4, 0);

const add = (a, b) => a + b;
const subtract = (a, b) => a - b;


class Glow extends Controller {
  static propTypes = {
    center: PropTypes.number.isRequired,
    bottom: PropTypes.number.isRequired,
    offset: PropTypes.number.isRequired,
    size: PropTypes.number.isRequired,
    shapeQuality: PropTypes.number.isRequired,
    glowHeightMod: PropTypes.number.isRequired,

    rippleSpeed: PropTypes.number.isRequired,

    blur: PropTypes.number.isRequired,

    transitionTime: PropTypes.number.isRequired,

    lowIntensityColor: PropTypes.array.isRequired,
    highIntensityColor: PropTypes.array.isRequired,
  }

  static defaultProps = {
    center: WIDTH,
    bottom: HEIGHT,
    offset: 80 * P,
    size: GLOW_WIDTH * 2,
    shapeQuality: P,
    glowHeightMod: 40 * P,

    rippleSpeed: 1,
    rippleAmplitude: 2.5 * P,

    blur: 15 * P,

    transitionTime: 3000,

    lowIntensityColor: COLORS.num.blue,
    highIntensityColor: COLORS.num.orange,
  }

  getPosition = () => (
    this.props.bottom + (this.props.offset * 1.1)
  )

  timestamp = 0
  position = this.getPosition()
  activity = 0
  activityHandler = new AnimationHandler(strongEaseOut, {
    startTime: this.timestamp,
    duration: this.props.transitionTime,
    loop: false,
    distance: 0,
    startingPosition: 0,
  })
  colorActivity = 0
  colorActivityHandler = new AnimationHandler(strongEaseOut, {
    startTime: this.timestamp,
    duration: this.props.transitionTime,
    loop: false,
    distance: 0,
    startingPosition: 0,
  })

  onActivityChange = ({ activity }) => {
    this.colorActivityHandler = new AnimationHandler(strongEaseOut, {
      startTime: this.timestamp,
      duration: this.props.transitionTime,
      loop: false,
      distance: activity - this.colorActivity,
      startingPosition: this.colorActivity,
    });
  }

  onLimitedActivityChange = ({ activity }) => {
    this.activityHandler = new AnimationHandler(strongEaseOut, {
      startTime: this.timestamp,
      duration: this.props.transitionTime,
      loop: false,
      distance: activity - this.activity,
      startingPosition: this.activity,
    });
  }

  ripple = (percent, toLeft) => {
    let op1 = subtract;
    let op2 = add;
    if (toLeft) {
      op1 = add;
      op2 = subtract;
    }
    return -(
      (
        Math.sin(op1(
          (percent * 17),
          this.props.rippleSpeed * (this.timestamp / 1346),
        )) * this.props.rippleAmplitude
      ) +
      (
        Math.cos(op2(
          (percent * 5),
          this.props.rippleSpeed * (this.timestamp / 1000),
        )) * this.props.rippleAmplitude
      )
    );
  }

  renderShape = () => {
    this.props.ctx.beginPath();
    const startPoint = this.props.center - (this.props.size / 2);
    for (let i = 0; i < this.props.size; i += this.props.shapeQuality) {
      const percent = i / this.props.size;
      this.props.ctx.lineTo(
        startPoint + i,
        (
          (
            this.position
            - (
              (
                (
                  -(((percent - 0.5) * 2) ** 2)
                  + (this.activity * 0.6) + 0.3
                )
              )
              * this.props.glowHeightMod
            )
          )
          + this.ripple(percent, i > this.props.size / 2)
        ),
      );
    }
    this.props.ctx.lineTo(startPoint + this.props.size, this.position);
    this.props.ctx.lineTo(startPoint, this.position);
    this.props.ctx.fill();
    this.props.ctx.closePath();
  }

  render = ({ timestamp }) => {
    this.timestamp = timestamp;

    this.activity = this.activityHandler.getPosition(timestamp);
    this.position = this.getPosition();

    this.colorActivity = this.colorActivityHandler.getPosition(timestamp);
    const newColor = [];
    for (let i = 0; i < 3; i++) {
      const low = this.props.lowIntensityColor[i];
      const high = this.props.highIntensityColor[i];
      newColor[i] = Math.floor(low + ((high - low) * this.colorActivity));
    }
    const color = newColor.join(',');

    this.props.ctx.fillStyle = '#000'; // must be set so alpha is full
    this.props.ctx.shadowColor = `rgba(${color},${0.15 + (0.1 * this.activity)})`;
    this.props.ctx.shadowBlur = this.props.blur;
    this.props.ctx.shadowOffsetY = -this.props.offset;

    this.renderShape();

    this.props.ctx.shadowColor = 'transparent';
    this.props.ctx.shadowBlur = 0;
    this.props.ctx.shadowOffsetY = 0;
  }
}


export default Glow;
