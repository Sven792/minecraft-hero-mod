import PropTypes from 'prop-types';

import { EventEmitter } from 'fbemitter';

import {
  EVENT_NAMES,
  LISTENER_NAMES,
} from '../constants';

class Controller {
  constructor(props) {
    // assign props
    this.props = props;

    // add default props
    if (this.constructor.defaultProps) {
      this.props = {
        ...this.constructor.defaultProps,
        ...this.props,
      };
    }

    // check prop types
    if (this.constructor.propTypes) {
      const propTypes = {
        ...this.constructor.propTypes,
        emitter: PropTypes.instanceOf(EventEmitter).isRequired,
        ctx: PropTypes.instanceOf(CanvasRenderingContext2D).isRequired,
      };

      PropTypes.checkPropTypes(
        propTypes,
        this.props,
        'prop',
        this.constructor.name,
      );
    }

    // hook up listeners to event emitter
    //   this task must be run as a timeout so it is run after the methods are attached to `this`
    setTimeout(() => {
      const eventNames = Object.keys(EVENT_NAMES);
      for (let i = 0; i < eventNames.length; i++) {
        const name = eventNames[i];
        if (this[LISTENER_NAMES[name]]) {
          this.props.emitter.addListener(EVENT_NAMES[name], this[LISTENER_NAMES[name]]);
        }
      }
    });
  }
}


export default Controller;
