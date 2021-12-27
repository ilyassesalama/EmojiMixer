# EmojiMixer
 An app that mixes emojis using Google Emoji Kitchen API mainly inspired from [emojimix](https://tikolu.net/emojimix/).
 The project is just broken since I'm not active anymore to fix it, I appreciate every contribution especially the ones that help to make the app more consistent.

## Preview
<img src="/Screenshots/emojismixer_preview.gif" width="300" height="600">

## To-do
- [ ] Emojis sliders. (still broken and needs improvements).

What's wrong/to add:
- First and last RecyclerView items are not centered which makes them inaccessible to mix.
- When scrolling both RecyclerViews fast sometimes shown mixed emojis is wrong.
- Progress bar is hiding for some emojis even when they're not loaded.
- Tapping an emoji should smooth scroll to it instead of making it unclickable.
- The spacing between emojis is not correct.
 
- [ ] Better URL checking system. (current system is fine but it doesn't fetch all emojis).

What's wrong/to add:
- We already have emojis release date, so it has to be used to fetch emojis instead of using reverse method to load the emoji as a workaround.
- New emojis have to be added since they were released but not implemented yet. (use emojimix repository and website as a refrence).

- [ ] Export mixed emojis as WhatsApp/Telegram stickers.
## Disclaimer
This app uses Google Keyboard emoji kitchen API and all emojis generated in the app are by Google.
