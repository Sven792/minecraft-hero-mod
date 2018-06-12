// define crop path types
const circle = 'circle';
const hexagon = 'hexagon';

// define input types
const imageTypes = [];
if (window.HTMLCanvasElement) imageTypes.push(HTMLCanvasElement);
if (window.HTMLImageElement) imageTypes.push(HTMLImageElement);
if (window.HTMLVideoElement) imageTypes.push(HTMLVideoElement);
if (window.ImageBitmap) imageTypes.push(ImageBitmap);

const checkType = (
  value,
  {
    instanceOfOneOf: instanceTypeList,
  },
) => {
  if (instanceTypeList) {
    for (let i = 0; i < instanceTypeList.length; i++) {
      if (value instanceof instanceTypeList[i]) {
        return true;
      }
    }
    return false;
  }
  throw new Error('no type specified');
};


/**
 * crops an image with a shape
 * @param {HTMLCanvasElement|HTMLImageElement|HTMLVideoElement|ImageBitmap} image
 *   the image element to crop
 * @param {string} with the shape to crop the image with (default: 'circle')
 */
const crop = (image, { with: shape = circle } = {}) => {
  // handle input type errors
  if (
    !image
    || typeof image !== 'object'
    || !checkType(image, { instanceOfOneOf: imageTypes })
  ) {
    throw new TypeError(`expected image to be of type ${
      imageTypes
        .map(v => v.name)
        .join(', ')
    }`);
  }

  // extract width and height from image
  const { width, height } = image;
  const halfWidth = width / 2;
  const halfHeight = height / 2;

  // set up canvas
  const can = document.createElement('canvas');
  can.width = width;
  can.height = height;
  const ctx = can.getContext('2d');

  // define the clip path based on the provided shape
  switch (shape) {
    case circle:
      ctx.arc(
        halfWidth,
        halfHeight,
        halfWidth,
        0,
        Math.PI * 2,
      );
      break;
    case hexagon:
      (() => {
        const distanceToSide = {
          x: Math.sin(Math.PI / 3) * halfWidth,
          y: Math.cos(Math.PI / 3) * halfWidth,
        };
        ctx.translate(halfWidth, halfHeight);
        ctx.beginPath();
        ctx.lineTo(distanceToSide.x, -distanceToSide.y);
        ctx.lineTo(0, -halfWidth);
        ctx.lineTo(-distanceToSide.x, -distanceToSide.y);
        ctx.lineTo(-distanceToSide.x, distanceToSide.y);
        ctx.lineTo(0, halfWidth);
        ctx.lineTo(distanceToSide.x, distanceToSide.y);
        ctx.closePath();
        ctx.translate(-halfWidth, -halfHeight);
      })();
      break;
    default:
      throw new Error(`shape of type ${shape} has not yet been implemented`);
  }

  // perform the clip
  ctx.clip();

  // draw the image
  ctx.drawImage(image, 0, 0, width, height);

  // return the cropped image
  return can;
};


export default crop;
