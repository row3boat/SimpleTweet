package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose : EditText
    lateinit var btnTweet : Button
    lateinit var charCount : TextView

    lateinit var client:TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        charCount = findViewById(R.id.tvTweetChars)
        btnTweet = findViewById(R.id.btnTweet)
        etCompose = findViewById(R.id.etTweetCompose)

        client = TwitterApplication.getRestClient(this)

        val defaultColor = charCount.textColors

        etCompose.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (etCompose.length() > 280){
                    charCount.setTextColor(Color.RED)
                    charCount.text = etCompose.length().toString() + " /280. \nOver char limit."
                }
                else {
                    charCount.setTextColor(defaultColor)
                    charCount.text = etCompose.length().toString() + " /280"
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
        })


        btnTweet.setOnClickListener {

            val tweetContent = etCompose.text.toString()

            if (tweetContent.isEmpty()){
                Toast.makeText(this, "Empty tweets are not allowed!", Toast.LENGTH_SHORT).show()
            } else {

                if (tweetContent.length > 280) {
                    Toast.makeText(
                        this,
                        "Tweet is too long!  Limit is 140 characters",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    client.publishTweet(tweetContent, object : JsonHttpResponseHandler(){

                        override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                            val tweet = Tweet.fromJson(json.jsonObject)

                            val intent = Intent()
                            intent.putExtra("tweet", tweet)
                            setResult(RESULT_OK, intent)
                            finish()
                        }


                        override fun onFailure(
                            statusCode: Int,
                            headers: Headers?,
                            response: String?,
                            throwable: Throwable?
                        ) {
                            Log.e(TAG, "failed to publish tweet", throwable)
                        }



                    })
                }
            }


        }
    }

    companion object {
        val TAG = "ComposeActivity"
    }
}