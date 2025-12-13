## Preface

Please read this document carefully to avoid subtle bugs related to non-matching coordinate systems.

The codebase uses multiple coordinate systems with different axis directions,
origins, and rotation directions. These differences are imposed by third-party
libraries (e.g. FTC SDK, Road Runner, hardware drivers) and cannot be changed.

Mixing coordinate systems without proper conversion leads to hard-to-debug-and-hard-to-understand
bugs that compromise the whole navigation system. This document aims to clarify the coordinate
systems and establish strict guidelines to avoid any issues.

## Guidelines

To avoid confusion and bugs, we adopt the following guidelines. Refer
to [Coordinate systems](#coordinate-systems) for details on each coordinate system.

- All internal logic must use the **Standard coordinate system** only.
- Library specific coordinate systems are allowed **only at boundaries**, i.e. when interfacing as
  input or output with a library. It **must** be converted to the Standard coordinate system before
  any further use. Any violation of this rule must be clearly documented.
- **Centralize** all coordinate system conversions in utility functions defined in the `Pose2D` (or
  related) class(es).
- Coordinate systems of new positional libraries must be **documented** in this file.

## Coordinate systems

### Standard coordinate system

- Origin: Center of the field
- X axis: Positive X points toward the audience side
- Y axis: Positive Y points toward the blue alliance side
- Zero rotation: Direction of the +X axis
- Rotation direction: Positive rotation is counter-clockwise
- Units: Any units, used through auto-converting classes (e.g. `Pose2D`, `Distance`, etc.)

### FTC coordinate system

- Origin: Center of the field
- X axis: Positive X points toward the audience side
- Y axis: Positive Y points toward the red alliance side
- Zero rotation: Direction of the +X axis
- Rotation direction: Positive rotation is counter-clockwise
- Units: Inches, degrees. Usually they are auto-converted upon access.

### Road Runner coordinate system

Relative to the robot

- Origin: Center of the robot
- X axis: Positive X points toward the right side of the robot
- Y axis: Positive Y points toward the front of the robot
- Rotation direction: Positive rotation is counter-clockwise
- Units: Radians. It does not impose any distance unit, so we use auto-converting classes (e.g.
  `Pose2D`, `Distance`, etc.)

### Gobilda Pinpoint Driver coordinate system

Can operator in either absolute or relative mode.

#### Absolute mode

The absolute mode uses the FTC coordinate system.

#### Relative mode

The relative mode is defined as follows:

- Origin: Center of the robot
- X axis: Positive X points toward the front of the robot
- Y axis: Positive Y points toward the left side of the robot
- Rotation direction: Positive rotation is counter-clockwise
- Units: Inches, degrees. Usually they are auto-converted upon access.
