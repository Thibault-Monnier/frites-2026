## Gamepad controls for the robot

To connect the gamepad to the controller, plug it in then press Home + Options + A (Cross)
simultaneously.

### Joysticks

- **Left Joystick**: Field-Centric Movement, seen from the team's perspective. Slow mode when
  pressed.
- **Right Joystick**: Rotation. Slow mode when pressed.

### Front Buttons

- **Left Bumper** while pressing: Turns towards the goal, ignoring rotation input.
- **Left Trigger** while pressing: Activates the intake mechanism.
- **Right Bumper** while pressing: Force shoots, whether or not the cannon is ready.
- **Right Trigger** while pressing: Shoots normally. If the cannon isn't ready, vibrates the
  controller. The controller LED is green when ready to shoot, red otherwise.

### Top Buttons

- **Y = Triangle** single press: Toggles the intake sorter left/right. Goes to the left if it was
  centered.
- **X = Square** single press: Toggles the shooter on/off. When off, the motors do NOT break.
- **A = Cross** while pressing: Runs the intake and the cannon buffers in reverse to eject balls.
  Does NOT affect the intake sorter.
- **B = Circle**:
    - Double press: Toggles super slow mode, used for moving to end position.
    - Long press: Resets the robot's position to the starting pose.
- **DPAD-DOWN** single press: Center the intake sorter.

## Shooting

The shooting starts with the side opposite to the intake sorter, to avoid shooting from the side
with only one ball. It then shoots right, then left.

The shots are spaced out by ~0.5 seconds. If the cannon isn't ready to shoot, the robot will wait
until it is ready before shooting the next ball. As soon as the shooting button is released, the
shooting state is reset (so the next time it will start this sequence from the beginning).

## Op Modes

For the competition, choose the "**Normal Auto** - [team color]" op mode, then preselect the "
**Normal Manual After Auto** - [team color]" op mode for teleop. This will ensure the position isn't
reset when switching between auto and teleop.

> NOTE: For Auto Op Mode, think about putting **two** balls left and **one** ball right.

For testing purposes, use the "**Normal Manual** - [team color]" op mode for teleop, which force
resets
to the starting pose.

If having problems with positioning, use the "**No Pose Calculation Manual** - [team color]" op
mode,
which disables all localization and uses default speed values.