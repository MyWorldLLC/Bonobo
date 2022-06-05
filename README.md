# Bonobo

Bonobo is an *experimental* game engine created in-house at MyWorld, LLC to trial building a high-level game engine with modern rendering technologies. It is inspired
by [jMonkeyEngine](https://jmonkeyengine.org/), though it is a fresh codebase and does not directly inherit any code from jME.
Bonobo is written in modern Java (18+) and utilizes the Vulkan API for rendering.

## Why Bonobo?
MyWorld has always used [jMonkeyEngine](https://jmonkeyengine.org/). While jME is a wonderful engine that has served us very well, it was originally
written for legacy (2 & 3) OpenGL, not for OpenGL 4+, and in addition it does not support Vulkan. GPUs and graphics APIs have changed a great deal since then, making
taking good advantage of them more complicated than simply adding a new renderer to jME. In addition to being written to target Vulkan, Bonobo is also intended to
experiment with some very fundamental quality-of-life changes for developers using it. If successful, these will make Bonobo a bit more user-friendly than jME can be
at times. Since jME is all about the monkeys, it seemed fitting to name an experimental offshoot in the same vein. Since Bonobos are some of the
[more human-friendly](https://en.wikipedia.org/wiki/Bonobo#Similarity_to_humans) of the great apes, naming an engine intended to be higher level and therefore more
human-friendly after them seemed a natural choice.

## Current State
Bonobo is in *very* early stages and is nowhere near close for real use of any sort. Have a good idea for a game engine? Fork Bonobo and make a pull request!

## Contributing
As above, please, by all means, fork the engine and make a pull request if you have a good, indifferent, or even lousy idea for a game engine! It's an experiment, after
all! As things progress we'll keep what we love, refactor what we don't, and discard whatever just doesn't fit well.

## License
Bonobo is licensed under the permissive Apache License 2.0.
