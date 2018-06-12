import PropTypes from 'prop-types';

import handle from '../../../utils/handler';

import Controller from '../common/Controller';

import {
  goToTarget,
  makePopHandler,
} from '../common';

import {
  EVENT_NAMES,
  COLORS,
  ALIGNMENTS,
  ALIGNMENTS_LIST,
} from '../constants';

// performer specific constants may only be used in defaultProps
import {
  P,
  WIDTH,
  HEIGHT,
} from '../constants/performer';

const PIXEL_SIZE_REGEX = /\$\(pixel-size\)/;

class Message extends Controller {
  static propTypes = {
    defaultFont: PropTypes.string.isRequired,
    defaultColor: PropTypes.string.isRequired,
    defaultSize: PropTypes.number.isRequired,

    position: PropTypes.number.isRequired,
    alignment: PropTypes.oneOf(ALIGNMENTS_LIST),
    height: PropTypes.number.isRequired,
    y: PropTypes.number.isRequired,
  }

  static defaultProps = {
    defaultFont: '$(pixel-size)px ministry, sans-serif',
    defaultColor: COLORS.hex.white,
    defaultSize: 6 * P,

    position: WIDTH - (5 * P),
    alignment: ALIGNMENTS.RIGHT,
    maxWidth: WIDTH - (10 * P),
    height: HEIGHT,
    y: 10 * P,
  }

  renderFontFactory = font => (
    (px, boldness = '') =>
      (font || this.props.defaultFont)
        .replace(PIXEL_SIZE_REGEX, `${boldness} ${Math.floor(px)}`)
  )

  message = {
    animation: {
      direction: 'off',
      handler: false,
    },
    current: {
      text: '',
      color: this.props.defaultColor,
      size: this.props.defaultSize,
      renderFont: this.renderFontFactory(),
    },
    next: false,
    remove: false,
  }
  timestamp = 0

  onMessage = ({
    text,
    font,
    color,
    size,
    remove,
    type,
    override,
    ...args
  }) => {
    if (
      !override
      && !(this.message.animation.direction === 'off')
      && (
        this.message.current.override
        || this.message.next.override
      )
    ) {
      return;
    }
    if (remove) {
      this.message.current.override = false;
      if (this.message.next) this.message.next = false;
      this.message.remove = true;
      return;
    }
    if (
      !this.message.remove
      && this.message.animation.handler
      && type === EVENT_NAMES.vote
      && type === this.message.current.type
    ) {
      return;
    }
    this.message.remove = false;
    this.message.next = {
      text,
      color: color || this.props.defaultColor,
      size: size || this.props.defaultSize,
      renderFont: this.renderFontFactory(font),
      type,
      override,
      ...args,
    };
  }

  getStartPosition = ({ width }) => {
    switch (this.props.alignment) {
      case ALIGNMENTS.CENTER:
        return this.props.position - (width / 2);
      case ALIGNMENTS.RIGHT:
        return this.props.position - width;
      case ALIGNMENTS.LEFT:
        return this.props.position;
      default:
        handle.error(new Error('Invalid alignment specified'));
        return 0;
    }
  }

  renderRep = ({ fontSize, height }) => {
    this.props.ctx.fillStyle = COLORS.hex.pink;
    this.props.ctx.font =
      this.message.current.renderFont(fontSize, 'bold');
    const { width } = this.props.ctx.measureText(this.message.current.text);
    const start = this.getStartPosition({ width: width + (fontSize * 3) });
    const textStart = start + (1.3 * fontSize);
    let innerWidth = 0;
    this.props.ctx.fillText(
      this.message.current.rep.textArray[0],
      textStart + innerWidth,
      height,
    );
    innerWidth +=
      this.props.ctx.measureText(this.message.current.rep.textArray[0]).width;
    this.props.ctx.fillStyle = COLORS.hex.white;
    this.props.ctx.font =
      this.message.current.renderFont(fontSize, 'lighter');
    this.props.ctx.fillText(
      this.message.current.rep.textArray[1],
      textStart + innerWidth,
      height,
    );
    innerWidth +=
      this.props.ctx.measureText(this.message.current.rep.textArray[1]).width;
    this.props.ctx.fillStyle = COLORS.hex.yellow;
    this.props.ctx.font =
      this.message.current.renderFont(fontSize, 'bold');
    this.props.ctx.fillText(
      this.message.current.rep.textArray[2],
      textStart + innerWidth,
      height,
    );

    const h = height - (fontSize * 0.85);

    this.props.ctx.drawImage(
      this.message.current.rep.img,
      start,
      h,
      fontSize,
      fontSize,
    );
    this.props.ctx.drawImage(
      this.message.current.rep.img,
      start + width + (fontSize * 1.5),
      h,
      fontSize,
      fontSize,
    );
  }

  renderViewcount = ({ fontSize, height }) => {
    this.props.ctx.fillStyle = this.message.current.color;
    this.props.ctx.font = this.message.current.renderFont(fontSize);
    const { width } = this.props.ctx.measureText(this.message.current.text);
    const start = this.getStartPosition({ width: width + (fontSize * 1.5) });
    const textStart = start + (1.3 * fontSize);
    this.props.ctx.fillText(
      this.message.current.text,
      textStart,
      height,
    );

    const h = height - (fontSize * 0.85);

    this.props.ctx.drawImage(
      this.message.current.viewcount.img,
      start,
      h,
      fontSize,
      fontSize,
    );
  }

  render = ({ timestamp, timediff }) => {
    this.timestamp = timestamp;

    if (this.message.remove === true) {
      if (this.message.animation.direction === 'in') {
        this.message.animation = {
          direction: 'out',
          handler: makePopHandler({ direction: 'out', timestamp }),
        };
      }
    } else if (this.message.next !== false) {
      if (
        this.message.next.type === this.message.current.type
        && !(this.message.animation.direction === 'off')
        && this.message.next.type === EVENT_NAMES.viewcount
      ) {
        if (!this.message.current.viewcountTransition) {
          this.message.current.viewcountTransition = this.message.current.text;
        }
        this.message.current.viewcountTransition = goToTarget(
          this.message.current.viewcountTransition,
          this.message.next.text,
          timediff,
          2,
        );
        this.message.current.text =
          Math.floor(this.message.current.viewcountTransition);
        if (this.message.current.viewcountTransition === this.message.next.text) {
          this.message.current = this.message.next;
          this.message.next = false;
        }
      } else if (this.message.animation.direction === 'in') {
        this.message.animation = {
          direction: 'out',
          handler: makePopHandler({ direction: 'out', timestamp }),
        };
      } else if (this.message.animation.direction === 'off') {
        this.message.animation = {
          direction: 'in',
          handler: makePopHandler({ direction: 'in', timestamp }),
        };
        this.message.current = this.message.next;
        this.message.next = false;
      }
    }
    if (this.message.animation.direction !== 'off') {
      const position = this.message.animation.handler.getPosition(timestamp);
      const height = this.props.height - (this.props.y * position);
      const fontSize = Math.min(
        this.message.current.size * position,
        this.message.current.size * 1.01,
      );

      if (this.message.current.rep) {
        this.renderRep({ fontSize, height });
      } else if (this.message.current.viewcount) {
        this.renderViewcount({ fontSize, height });
      } else {
        this.props.ctx.fillStyle = this.message.current.color;
        this.props.ctx.font = this.message.current.renderFont(fontSize);
        const { width } = this.props.ctx.measureText(this.message.current.text);
        this.props.ctx.fillText(
          this.message.current.text,
          this.getStartPosition({ width: Math.min(this.props.maxWidth, width) }),
          height,
          this.props.maxWidth,
        );
      }

      if (this.message.animation.direction === 'out' && position === 0) {
        this.message.remove = false;
        this.message.animation = {
          direction: 'off',
          handler: false,
        };
      }
    }
  }
}


export default Message;
