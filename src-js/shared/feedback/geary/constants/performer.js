// point size (modify to change the size of the displayed components)
//   (there only needs to be one if the ratio on screen is the same as the WIDTH/HEIGHT ratio)
export const P = 8;

// width and height should be used by controllers to anchor to positions in the canvas
//   NOTE: they should NOT be used for sizing as they are not related to the point size
export const WIDTH = 1024;
export const HEIGHT = 512;

export const HALF_WIDTH = WIDTH / 2;
export const HALF_HEIGHT = HEIGHT / 2;

export const BUBBLE_SPAWN_Y = HEIGHT - (8 * P);
export const BUBBLE_SPAWN_W = WIDTH - (40 * P);
export const BUBBLE_SPAWN_H = 6 * P;

export const GLOW_WIDTH = WIDTH - (10 * P);
