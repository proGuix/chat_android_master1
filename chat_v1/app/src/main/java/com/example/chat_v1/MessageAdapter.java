package com.example.chat_v1;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MessageAdapter extends ArrayAdapter<Message> {

	private ListView listView;
	private TextView dateView;
	private ImageView picture_profileView;
	private TextView authorView;
	private TextView messageView;
	private ImageView imageView;
	private List<Message> listMessage;
	private Activity activity;
	private Bitmap image;
	private List<Boolean> listPrintDate;
	private Boolean boolDateOneMax;
	private int indexDateWhichAppear;
	private int indexDateWhichDisappear;
	private boolean appearOneTime;
	private Object lock = new Object();
	
	public MessageAdapter(Context applicationContext, int layoutMessage, ListView listView) {
		super(applicationContext, layoutMessage);
		this.activity = (Activity) applicationContext;
		this.listView = listView;
		this.listMessage = new ArrayList<Message>();
		this.listPrintDate = new ArrayList<Boolean>();
		this.boolDateOneMax = false;
		this.indexDateWhichAppear = -1;
		this.indexDateWhichDisappear = -1;
		appearOneTime = false;
		// TODO Auto-generated constructor stub
	}

	public void add(Message message) {
		// TODO Auto-generated method stub
		this.listMessage.add(message);
		this.listPrintDate.add(false);
		super.add(message);
	}

	public void removeAll(){
		for(int i = listMessage.size()-1; i > -1; i--){
			this.listMessage.remove(0);
			this.listPrintDate.remove(0);
		}
	}
	
	public int getCount(){
		return this.listMessage.size();
	}
	
	public Message getItem(int index){
		if(getCount() == 0){
			return null;
		}
		return this.listMessage.get(index);
	}

	public Message getItemByPseudo(String pseudo){
		int size = getCount();
		for(int i = 0; i < size; i++){
			Message mess = getItem(i);
			if(mess.getAuthor().equals(pseudo)){
				return mess;
			}
		}
		return null;
	}

	public View getView(final int position, final View convertView, ViewGroup parent){
		View v = convertView;
		LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Message message = getItem(position);
		if(message == null){
			return v;
		}

		if(message.getSide() == Message.Side.LEFT){
			v = inflater.inflate(R.layout.layout_message_left, parent, false);
			authorView = (TextView) v.findViewById(R.id.author);
			authorView.setText(message.getAuthor());

			picture_profileView = (ImageView) v.findViewById(R.id.picture_profile);
			Bitmap picture_profileBMP = message.getPictureProfile();
			if(picture_profileBMP != null){
				DisplayMetrics displaymetrics = new DisplayMetrics();
				activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				double width = displaymetrics.widthPixels;
				double fixeWidth = (width * 0.1);
				int nh = (int)(picture_profileBMP.getHeight() * (fixeWidth / picture_profileBMP.getWidth()));
				picture_profileView.setImageBitmap(Bitmap.createScaledBitmap(picture_profileBMP, (int) fixeWidth, nh, false));
			}
			else{
				int id = activity.getResources().getIdentifier("ic_face_black_36dp", "drawable", activity.getPackageName());
				picture_profileView.setImageResource(id);
			}
		}
		else{
			v = inflater.inflate(R.layout.layout_message_right, parent, false);
		}

		dateView = (TextView) v.findViewById(R.id.date_message);
		String temps = message.getTime().substring(0, 16);
		dateView.setText(temps);
		if(this.listPrintDate.get(position)){
			if(!appearOneTime){
				Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_date_appear);
				dateView.startAnimation(animation);
				appearOneTime = true;
			}
			dateView.setVisibility(View.VISIBLE);
		}
		else{
			if(position == indexDateWhichDisappear){
				Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_date_disappear);
				dateView.startAnimation(animation);
				this.indexDateWhichDisappear = -1;
			}
			dateView.setVisibility(View.GONE);
			((ViewGroup) dateView.getParent()).removeView(dateView);
		}

		View.OnClickListener appearDisappearDateOnClick = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Boolean stateCurrentDate = listPrintDate.get(position);
				if(!boolDateOneMax){
					boolDateOneMax = !boolDateOneMax;
					indexDateWhichAppear = position;
					appearOneTime = false;
				}
				else{
					if(stateCurrentDate){
						boolDateOneMax = !boolDateOneMax;
						indexDateWhichDisappear = indexDateWhichAppear;
					}
					else{
						indexDateWhichAppear = position;
						appearOneTime = false;
						for(int i = 0; i < listPrintDate.size(); i++){
							listPrintDate.set(i, false);
						}
					}
				}
				listPrintDate.set(position, !stateCurrentDate);
				notifyDataSetChanged();
				int index = listView.getSelectedItemPosition();
				listView.setSelection(index);
			}
		};

		image = message.getImage();
		if(image != null){
			imageView = (ImageView) v.findViewById(R.id.image_message);

			DisplayMetrics displaymetrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			double width = displaymetrics.widthPixels;
			double fixeWidth = (width * 0.5);
			int nh = (int)(image.getHeight() * (fixeWidth / image.getWidth()));
			imageView.setImageBitmap(Bitmap.createScaledBitmap(image, (int) fixeWidth, nh, false));

			imageView.setTag(position);

			imageView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					int pos = (int)view.getTag();
					String temps = getItem(pos).getTime();
					Bitmap bmp = ((BitmapDrawable)((ImageView)view).getDrawable()).getBitmap();
					DialogFragment newFragment = PictureDialog.newInstance(bmp, temps);
					newFragment.show(((Activity) getContext()).getFragmentManager(), "picture_dialog");
					return true;
				}
			});

			imageView.setOnClickListener(appearDisappearDateOnClick);
		}
		else{
			imageView = (ImageView) v.findViewById(R.id.image_message);
			((ViewGroup) imageView.getParent()).removeView(imageView);
		}

		String texte = message.getMessage();
		if(texte != null){
			messageView = (TextView) v.findViewById(R.id.message);
			messageView.setText(texte);

			messageView.setOnClickListener(appearDisappearDateOnClick);
		}
		else {
			messageView = (TextView) v.findViewById(R.id.message);
			((ViewGroup) messageView.getParent()).removeView(messageView);
		}

		return v;
	}
}
