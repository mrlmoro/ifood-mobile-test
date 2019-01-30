package br.com.murilomoro.tweetlikes.mock

import br.com.murilomoro.tweetlikes.data.local.sentiment.SentimentEntity
import com.google.api.services.language.v1.model.Sentiment
import com.google.gson.Gson
import com.twitter.sdk.android.core.models.Tweet

/**
 * Created by Murilo Moro on 28/01/19.
 */

fun getTweetMock(text: String = HAPPY_TEXT): Tweet {
    return Gson().fromJson(tweetJsonMock(text), Tweet::class.java)
}

fun getSentimentEntityMock(score: Float) = SentimentEntity(
    getTweetMock().idStr,
    score,
    0f
)

fun getSentimentMock(score: Float): Sentiment {
    val sentiment = Sentiment()
    sentiment.score = score
    sentiment.magnitude = 0f
    return sentiment
}

const val HAPPY_TEXT = "I am so HAPPY!"
const val SAD_TEX = "I am so SAD!"
const val NEUTRAL_TEXT = "I am so NEUTRAL!"

private fun tweetJsonMock(text: String) = "{\n" +
        "        \"created_at\": \"Mon Jan 28 23:41:03 +0000 2019\",\n" +
        "        \"id\": 1090032033789865984,\n" +
        "        \"id_str\": \"1090032033789865984\",\n" +
        "        \"text\": \"$text\",\n" +
        "        \"truncated\": false,\n" +
        "        \"entities\": {\n" +
        "            \"hashtags\": [],\n" +
        "            \"symbols\": [],\n" +
        "            \"user_mentions\": [\n" +
        "                {\n" +
        "                    \"screen_name\": \"luanccoelho\",\n" +
        "                    \"name\": \"luan c\",\n" +
        "                    \"id\": 1270157442,\n" +
        "                    \"id_str\": \"1270157442\",\n" +
        "                    \"indices\": [\n" +
        "                        0,\n" +
        "                        12\n" +
        "                    ]\n" +
        "                }\n" +
        "            ],\n" +
        "            \"urls\": []\n" +
        "        },\n" +
        "        \"source\": \"<a href=\\\"http://twitter.com/download/iphone\\\" rel=\\\"nofollow\\\">Twitter for iPhone</a>\",\n" +
        "        \"in_reply_to_status_id\": 1090031820396212229,\n" +
        "        \"in_reply_to_status_id_str\": \"1090031820396212229\",\n" +
        "        \"in_reply_to_user_id\": 1270157442,\n" +
        "        \"in_reply_to_user_id_str\": \"1270157442\",\n" +
        "        \"in_reply_to_screen_name\": \"luanccoelho\",\n" +
        "        \"user\": {\n" +
        "            \"id\": 292383655,\n" +
        "            \"id_str\": \"292383655\",\n" +
        "            \"name\": \"iFood\",\n" +
        "            \"screen_name\": \"iFood\",\n" +
        "            \"location\": \"Brasil\",\n" +
        "            \"description\": \"Pra qualquer fome, já sabe: Pede um iFood.\",\n" +
        "            \"url\": \"http://t.co/osLQydpNMt\",\n" +
        "            \"entities\": {\n" +
        "                \"url\": {\n" +
        "                    \"urls\": [\n" +
        "                        {\n" +
        "                            \"url\": \"http://t.co/osLQydpNMt\",\n" +
        "                            \"expanded_url\": \"http://www.ifood.com.br\",\n" +
        "                            \"display_url\": \"ifood.com.br\",\n" +
        "                            \"indices\": [\n" +
        "                                0,\n" +
        "                                22\n" +
        "                            ]\n" +
        "                        }\n" +
        "                    ]\n" +
        "                },\n" +
        "                \"description\": {\n" +
        "                    \"urls\": []\n" +
        "                }\n" +
        "            },\n" +
        "            \"protected\": false,\n" +
        "            \"followers_count\": 113716,\n" +
        "            \"friends_count\": 8774,\n" +
        "            \"listed_count\": 163,\n" +
        "            \"created_at\": \"Tue May 03 15:24:00 +0000 2011\",\n" +
        "            \"favourites_count\": 8823,\n" +
        "            \"utc_offset\": null,\n" +
        "            \"time_zone\": null,\n" +
        "            \"geo_enabled\": true,\n" +
        "            \"verified\": true,\n" +
        "            \"statuses_count\": 35507,\n" +
        "            \"lang\": \"pt\",\n" +
        "            \"contributors_enabled\": false,\n" +
        "            \"is_translator\": false,\n" +
        "            \"is_translation_enabled\": false,\n" +
        "            \"profile_background_color\": \"9A2529\",\n" +
        "            \"profile_background_image_url\": \"http://abs.twimg.com/images/themes/theme17/bg.gif\",\n" +
        "            \"profile_background_image_url_https\": \"https://abs.twimg.com/images/themes/theme17/bg.gif\",\n" +
        "            \"profile_background_tile\": false,\n" +
        "            \"profile_image_url\": \"http://pbs.twimg.com/profile_images/887672393648414720/TVMqDVLD_normal.jpg\",\n" +
        "            \"profile_image_url_https\": \"https://pbs.twimg.com/profile_images/887672393648414720/TVMqDVLD_normal.jpg\",\n" +
        "            \"profile_banner_url\": \"https://pbs.twimg.com/profile_banners/292383655/1547723880\",\n" +
        "            \"profile_link_color\": \"9A2529\",\n" +
        "            \"profile_sidebar_border_color\": \"FFFFFF\",\n" +
        "            \"profile_sidebar_fill_color\": \"E6F6F9\",\n" +
        "            \"profile_text_color\": \"333333\",\n" +
        "            \"profile_use_background_image\": false,\n" +
        "            \"has_extended_profile\": false,\n" +
        "            \"default_profile\": false,\n" +
        "            \"default_profile_image\": false,\n" +
        "            \"following\": false,\n" +
        "            \"follow_request_sent\": false,\n" +
        "            \"notifications\": false,\n" +
        "            \"translator_type\": \"none\"\n" +
        "        },\n" +
        "        \"geo\": null,\n" +
        "        \"coordinates\": null,\n" +
        "        \"place\": null,\n" +
        "        \"contributors\": null,\n" +
        "        \"is_quote_status\": false,\n" +
        "        \"retweet_count\": 1,\n" +
        "        \"favorite_count\": 2,\n" +
        "        \"favorited\": false,\n" +
        "        \"retweeted\": false,\n" +
        "        \"lang\": \"pt\"\n" +
        "    }"