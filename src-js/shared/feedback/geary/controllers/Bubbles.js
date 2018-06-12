import PropTypes from 'prop-types';

import handle from '../../../utils/handler';

import Controller from '../common/Controller';

import { ALIGNMENTS, ALIGNMENTS_LIST } from '../constants';
import { HALF_PI } from '../constants/math';

// performer specific constants may only be used in defaultProps
import {
  P,
  BUBBLE_SPAWN_Y,
  BUBBLE_SPAWN_W,
  BUBBLE_SPAWN_H,
} from '../constants/performer';


class Bubbles extends Controller {
  static propTypes = {
    size: PropTypes.number.isRequired,

    spawnBox: PropTypes.shape({
      x: PropTypes.number.isRequired,
      y: PropTypes.number.isRequired,
      w: PropTypes.number.isRequired,
      h: PropTypes.number.isRequired,
    }).isRequired,

    spawnAngle: PropTypes.number.isRequired,
    spriteAngle: PropTypes.number.isRequired,

    minLifespan: PropTypes.number.isRequired,
    additionalLifespan: PropTypes.number.isRequired,

    minDistance: PropTypes.number.isRequired,
    additionalDistance: PropTypes.number.isRequired,

    rotationLeeway: PropTypes.number.isRequired,

    alignment: PropTypes.oneOf(ALIGNMENTS_LIST).isRequired,
  }

  static defaultProps = {
    size: 5 * P,

    minWidthPercentage: 0.2,

    spawnBox: {
      x: 35 * P,
      y: BUBBLE_SPAWN_Y,
      w: BUBBLE_SPAWN_W,
      h: BUBBLE_SPAWN_H,
    },

    spawnAngle: 0,
    spriteAngle: 0, // relative to spawnAngle

    minLifespan: 780,
    additionalLifespan: 600,

    minDistance: 80 * P,
    additionalDistance: 160 * P,

    rotationLeeway: Math.PI / 8,

    alignment: ALIGNMENTS.RIGHT,
  }

  timestamp = 0;
  bubbles = [];
  widthPercentage = this.props.minWidthPercentage;

  onLimitedActivityChange = ({ activity }) => {
    this.activity = activity;

    this.widthPercentage = (
      (
        (
          Math.max(1 - this.props.minWidthPercentage, 0)
        )
        * this.activity
      )
      + this.props.minWidthPercentage
    );
  }

  onBubble = ({ id }) => {
    this.props.assets.load({ local: id })
      .then((img) => {
        const { spawnBox } = this.props;
        const x = this.getBubbleStartX();
        const y = ((Math.random() * spawnBox.h) + spawnBox.y);
        this.bubbles.push({
          x,
          y,
          img,
          alpha: 0,
          angle: (
            this.props.spawnAngle
            + (
              (Math.random() * (this.props.rotationLeeway * 2))
              - this.props.rotationLeeway
            )
          ),
          timestamp: this.timestamp,
        });
      })
      .catch(handle.error);
  }

  getBubbleStartX = () => {
    const { alignment, spawnBox } = this.props;
    const randomXInWidth = (Math.random() * spawnBox.w * this.widthPercentage);
    switch (alignment) {
      case ALIGNMENTS.CENTER:
        return (
          randomXInWidth +
          spawnBox.x + ((spawnBox.w * (1 - this.widthPercentage)) / 2)
        );
      case ALIGNMENTS.RIGHT:
        return (spawnBox.x + spawnBox.w) - randomXInWidth;
      case ALIGNMENTS.LEFT:
        return randomXInWidth + spawnBox.x;
      default:
        handle.error(new Error('Invalid alignment specified'));
        return 0;
    }
  }

  calculateMovement = ({
    operator,
    angle,
    timediff,
    inversePercent,
  }) => (
    (((Math[operator](angle - HALF_PI) * 20) / this.props.minLifespan) / 500)
      * (
        this.props.minDistance
        + (this.props.additionalDistance * inversePercent)
      )
      * timediff
  )

  render = ({ timestamp, timediff }) => {
    this.timestamp = timestamp;

    for (let i = 0; i < this.bubbles.length; i++) {
      const bubble = this.bubbles[i];
      const lifetime = timestamp - bubble.timestamp;
      let inversePercent = ((20 - this.bubbles.length) / 20);
      if (20 - this.bubbles.length < 0) inversePercent = 0;
      const lifeLimit = (
        this.props.minLifespan
        + (inversePercent * this.props.additionalLifespan)
      );
      if (lifetime > lifeLimit) {
        this.bubbles.splice(i, 1);
        i--;
      } else {
        const currentLife = (lifetime < lifeLimit * 0.17 ?
          ((lifetime / lifeLimit) ** 2) * (1 / (0.17 ** 2)) :
          (1.15 * (0.16 / (lifetime / lifeLimit))) - 0.15) * 2;
        bubble.alpha = currentLife > 1 ? 1 : currentLife;
        const size = ((currentLife * 0.35) + 0.3) * this.props.size;
        bubble.x += this.calculateMovement({
          operator: 'cos',
          angle: bubble.angle,
          timediff,
          inversePercent,
        });
        bubble.y += this.calculateMovement({
          operator: 'sin',
          angle: bubble.angle,
          timediff,
          inversePercent,
        });
        this.props.ctx.globalAlpha = bubble.alpha;
        this.props.ctx.translate(bubble.x, bubble.y);
        this.props.ctx.rotate(this.props.spriteAngle + bubble.angle);
        this.props.ctx.drawImage(
          bubble.img,
          -(size / 2),
          -(size / 2),
          size,
          size,
        );
        this.props.ctx.rotate(-(this.props.spriteAngle + bubble.angle));
        this.props.ctx.translate(-bubble.x, -bubble.y);
      }
    }
    this.props.ctx.globalAlpha = 1;
  }
}


export default Bubbles;
