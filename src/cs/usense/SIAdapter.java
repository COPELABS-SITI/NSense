/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the USense application. It provides support for Inference module, USense activity and 
 * provides the functionality to update the SocialityDetails.
 * @author Saeik Firdose (COPELABS/ULHT)
 * @author Luis Lopes (COPELABS/ULHT)
 */

package cs.usense;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cs.usense.inferenceModule.SocialityDetails;

/**
 * This class provides methods to instantly update the SocialityDetails in the Usense activity
 */
public class SIAdapter extends ArrayAdapter<SocialityDetails>{

	private static final String TAG = "SI Adapter";
	
	/** Interface to global information about an application environment. */
	Context context;
	
	/** Layout Resource Id */
	int layoutResourceId;
	
	/** Defining SocialityDetails List object */
	List<SocialityDetails> data = null;
	
	/** Usense Activity module */
	UsenseActivity callback = null;

	/**
	 * This method constructs the Social Interaction Adapter from the context and by providing SocialityDetails object, layout and activity information
	 * @param context Interface to global information about an application environment. 
	 * @param data List with SocialityDetails
	 * @param layoutResourceId Layout of the SocialityDetails
	 * @param listActivity UsenseActivity
	 */
	public SIAdapter(Context context, List<SocialityDetails> data, int layoutResourceId,  UsenseActivity listActivity) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
		this.callback = listActivity;
	}


	/**
	 * This class provides the maximum social interaction
	 * @return maxSi Maximum social interaction
	 */
	public double getMaxSI(){
		double maxSi=0;

		if(data==null)return 0;
		for(int i=0;i<data.size();i++){
			SocialityDetails details=data.get(i);
			if(details.getmSI()>maxSi){
				maxSi=details.getmSI();
			}
		}
		Log.i(TAG, "Max SIvalue " +maxSi);
		return maxSi;
	}

	/**
	 * This method provides the maximum propinquity
	 * @return maxProp Maximum propinquity
	 */
	public double getMaxProp(){
		double maxProp=0;
		if(data==null)return 0;
		for(int i=0;i<data.size();i++){
			SocialityDetails details=data.get(i);
			if(details.getmPropinquity()>maxProp){
				maxProp=details.getmPropinquity();

			}
		}
		Log.i(TAG, "Max Propvalue " +maxProp);
		return maxProp;

	}

	/**
	 * This method provides the average social interaction
	 * @return avgSI Average Social interaction
	 */
	public double avgSI(){

		double avgSI = 0.0;
		double sumSI = 0.0;

		for(int i=0;i<data.size();i++){
			SocialityDetails details=data.get(i);

			sumSI = sumSI + details.getmSI();
		}

		avgSI = sumSI/data.size();

		return avgSI;
	}

	/**
	 * This method provides the average propinquity
	 * @return avgProp Average Propinquity
	 */
	public double avgProp(){


		double avgProp = 0.0;
		double sumProp = 0.0;
		for(int i=0;i<data.size();i++){
			SocialityDetails details=data.get(i);

			sumProp = sumProp + details.getmPropinquity();


		}

		avgProp = sumProp / data.size();
		
		return avgProp;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		EntryHolder holder = null;

		if(convertView == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();

			convertView = inflater.inflate(layoutResourceId, parent, false);

			holder = new EntryHolder();
			holder.txtTitle = (TextView)convertView.findViewById(R.id.txtTitle);
			holder.txtDistance = (TextView)convertView.findViewById(R.id.txtDistance);
			holder.imgStar1 = ((ImageView)convertView.findViewById(R.id.row_ratingStars1));
			holder.imgStar2 = ((ImageView)convertView.findViewById(R.id.row_ratingStars2));
			holder.imgStar3 = ((ImageView)convertView.findViewById(R.id.row_ratingStars3));
			holder.imgStar4 = ((ImageView)convertView.findViewById(R.id.row_ratingStars4));
			holder.imgStar5 = ((ImageView)convertView.findViewById(R.id.row_ratingStars5));

			convertView.setTag(holder);
		}
		else
		{
			holder = (EntryHolder)convertView.getTag();
		}

		if(data != null){

			SocialityDetails entry = data.get(position);

			if(entry != null){
				Log.i(TAG, "entry.getDevName() is  ##" + entry.getDevName());

				Log.i(TAG, "entry.getDevName().isEmpty() is it true ##" + entry.getDevName().isEmpty());
				if (!entry.getDevName().isEmpty())
					holder.txtTitle.setText(entry.getDevName());
				else
					holder.txtTitle.setText("No name");

				if(entry.getmDistance() == 0.0){
					holder.txtDistance.setText("Computing Distance\u2026");
				}
				else if (entry.getmDistance() != -1 ){
					holder.txtDistance.setText(String.format("%.2f", entry.getmDistance()) + " meters");
				}
				else
					holder.txtDistance.setText("Computing Distance\u2026");

				int stars = (int) (5*entry.getmSI()/getMaxSI());
				switch (stars) {
				case 0:
					holder.imgStar1.setImageResource(R.drawable.no_star_rating_small);
					holder.imgStar2.setImageResource(R.drawable.no_star_rating_small);
					holder.imgStar3.setImageResource(R.drawable.no_star_rating_small);
					holder.imgStar4.setImageResource(R.drawable.no_star_rating_small);
					holder.imgStar5.setImageResource(R.drawable.no_star_rating_small);
					break;
				case 1:
					holder.imgStar1.setImageResource(R.drawable.star_rating_small);
					holder.imgStar2.setImageResource(R.drawable.no_star_rating_small);
					holder.imgStar3.setImageResource(R.drawable.no_star_rating_small);
					holder.imgStar4.setImageResource(R.drawable.no_star_rating_small);
					holder.imgStar5.setImageResource(R.drawable.no_star_rating_small);
					break;
				case 2:
					holder.imgStar1.setImageResource(R.drawable.star_rating_small);
					holder.imgStar2.setImageResource(R.drawable.star_rating_small);
					holder.imgStar3.setImageResource(R.drawable.no_star_rating_small);
					holder.imgStar4.setImageResource(R.drawable.no_star_rating_small);
					holder.imgStar5.setImageResource(R.drawable.no_star_rating_small);
					break;
				case 3:
					holder.imgStar1.setImageResource(R.drawable.star_rating_small);
					holder.imgStar2.setImageResource(R.drawable.star_rating_small);
					holder.imgStar3.setImageResource(R.drawable.star_rating_small);
					holder.imgStar4.setImageResource(R.drawable.no_star_rating_small);
					holder.imgStar5.setImageResource(R.drawable.no_star_rating_small);
					break;
				case 4:
					holder.imgStar1.setImageResource(R.drawable.star_rating_small);
					holder.imgStar2.setImageResource(R.drawable.star_rating_small);
					holder.imgStar3.setImageResource(R.drawable.star_rating_small);
					holder.imgStar4.setImageResource(R.drawable.star_rating_small);
					holder.imgStar5.setImageResource(R.drawable.no_star_rating_small);
					break;
				case 5:
					holder.imgStar1.setImageResource(R.drawable.star_rating_small);
					holder.imgStar2.setImageResource(R.drawable.star_rating_small);
					holder.imgStar3.setImageResource(R.drawable.star_rating_small);
					holder.imgStar4.setImageResource(R.drawable.star_rating_small);
					holder.imgStar5.setImageResource(R.drawable.star_rating_small);
					break;
				default:
					holder.imgStar1.setImageResource(R.drawable.no_star_rating_small);
					holder.imgStar2.setImageResource(R.drawable.no_star_rating_small);
					holder.imgStar3.setImageResource(R.drawable.no_star_rating_small);
					holder.imgStar4.setImageResource(R.drawable.no_star_rating_small);
					holder.imgStar5.setImageResource(R.drawable.no_star_rating_small);
				}
			}
		}

		return convertView;
	}

	/**
	 * This class holds the entry of the text and image view
	 */
	static class EntryHolder
	{
		TextView txtTitle;
		TextView txtDistance;
		ImageView imgStar1;
		ImageView imgStar2;
		ImageView imgStar3;
		ImageView imgStar4;
		ImageView imgStar5;
	}

}
