package com.example.fastjudge;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterActivity extends Activity implements OnClickListener{

	private Button tweet;
	private Button back;
	private String textViewStr;
	private Twitter tw;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter);
		tweet=(Button)findViewById(R.id.tweet_button);
		back=(Button)findViewById(R.id.back_button);
		TextView tv=(TextView)findViewById(R.id.multiAutoCompleteTextView1);
		textViewStr=tv.getText().toString();

		
        if (!TwitterUtils.hasAccessToken(this)) {
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
            finish();
        }else{
        	//twitterオブジェクトの作成
        	tw = new TwitterFactory().getInstance();
        	 
        	String tokenSecretStr=getResources().getString(R.string.access_token_secret);
        	String tokenStr=getResources().getString(R.string.access_token);
        	String consumerKey=getResources().getString(R.string.twitter_consumer_key);
        	String consumerKeySecreat=getResources().getString(R.string.twitter_consumer_secret);
        	
			//AccessTokenオブジェクトの作成
        	AccessToken at = new AccessToken(tokenStr, tokenSecretStr);
        	 
        	//Consumer keyとConsumer key seacretの設定
        	tw.setOAuthConsumer(consumerKey, consumerKeySecreat);
        	 
        	//AccessTokenオブジェクトを設定
        	tw.setOAuthAccessToken(at);

        }
       	tweet.setOnClickListener(this);//アクションリスナーをつけてね
	}
	
    @Override
    public void onClick(View view){
    	if(view==tweet)
       	try {
       	    tw.updateStatus(textViewStr);
       	} catch (TwitterException e) {
       	    e.printStackTrace();
       	    if(e.isCausedByNetworkIssue()){
       	         Toast.makeText(this, "ネットーワークの問題です", Toast.LENGTH_LONG);
       	    }        	
       	    }
       
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.twitter, menu);
		return true;
	}
	

	}
	

