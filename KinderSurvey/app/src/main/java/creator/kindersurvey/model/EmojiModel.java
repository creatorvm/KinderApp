package creator.kindersurvey.model;

/**
 * Created by Development-2 on 24-11-2017.
 */

public class EmojiModel {
    int emojiId = 0;
    String emojiText = "";
    int emojiRating = 0;

    public int getEmojiId() {
        return emojiId;
    }

    public void setEmojiId(int emojiId) {
        this.emojiId = emojiId;
    }

    public String getEmojiText() {
        return emojiText;
    }

    public void setEmojiText(String emojiText) {
        this.emojiText = emojiText;
    }

    public int getEmojiRating() {
        return emojiRating;
    }

    public void setEmojiRating(int emojiRating) {
        this.emojiRating = emojiRating;
    }
}
