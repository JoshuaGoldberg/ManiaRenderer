## ON HOLD FOR NOW! Semi useable, but more as a novelty than a fully fledged tool. Still, feel free to use it! Hopefully I'll be able to add more to it later.

Welcome to Crabchip's Mania Renderer!

Converts replay files into a mp4 format (requires audio and map file as well).
Requires FFmpeg to create the mp4 file. Comes packaged with the released file, which should work as intended without any other necessary installations.

Currently, a WIP. Base functions work, slowly trying to improve hit timings and accuracy.
Uses the osu!lazer accuracy system, as stable's accuracy is more of a headache to properly calculate on ln's.

I hope to continue updating and improving this render as time goes on, though that all depends on how busy I am.

## HOW TO USE
1. Select the .osr file, the .osu file, and the audio file for rendering
2. Select additional settings for render (reduced fps/resolution for faster rendering, Nvidia driver utilization, etc.)
3. Wait for the renderer to construct the video (may take some time depending on framerate and song length)
4. The finished video will open upon finishing, and will be saved in the replay renderer's folder!

## TO-DO List
1. Improve accuracy. Currently, accuracy is mostly correct, although there's some issues when dealing with ln's that I would love to see fixed.
2. Improve/Add mod support. Halftime and doubletime have basic implementations, but accuracy struggles even more when trying to work with them.
3. Allow support for both lazer and stable scoring systems
4. Streamline/Improve GUI (perhaps a settings menu is in order)
5. Add additional support for cosmetic changes to the actual rendering (allow custom skins, if I'm lucky)
6. Switch to a better and more efficient rendering engine (If I'm feeling really motivated)
