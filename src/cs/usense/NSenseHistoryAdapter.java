/**
 * @version 1.3
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It provides support to the NSense History Activity, and 
 * provides the list of devices, SocialInteraction, and Propinquity information with the layout.
 * @author Saeik Firdose (COPELABS/ULHT)
 * @author Luis Lopes (COPELABS/ULHT)
 * 
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
import cs.usense.R;
import cs.usense.inferenceModule.SocialityDetails;

/**
 * This class provides adapter details to the NSenseHistoryActivity 
 */
public class NSenseHistoryAdapter extends ArrayAdapter<SocialityDetails>{

	private static final String TAG = "NSenseHistoryAdapter";
	
	/** Interface to global information about an application environment. */
	Context context;
	
	/** Layout Resource Id */
	int layoutResourceId;
	
	/** List of SocialityDetails objects to get the dataa */
	List<SocialityDetails> data = null;
	
	/** NSense Activity class */
	NSenseActivity callback = null;
	
	/**
	 * This method construct the NSenseHistoryAdapter with the context provided by NSense Activity class
	 * @param context Interface to global information about an application environment
	 * @param data List with SocialityDetails
	 * @param layoutResourceId Layout of the SocialityDetails
	 * @param listActivity NSenseActivity
	 */
	public NSenseHistoryAdapter(Context context, List<SocialityDetails> data, int layoutResourceId,  NSenseActivity listActivity) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
		this.callback = listActivity;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {

        EntryHolder holder = null;
        Log.i(TAG, "Entered in to get view method" +"");
        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            
            convertView = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new EntryHolder();
            holder.txtTitle = (TextView)convertView.findViewById(R.id.txtTitle);
            holder.imgStar1 = ((ImageView)convertView.findViewById(R.id.row_ratingStars1));
    		holder.imgStar2 = ((ImageView)convertView.findViewById(R.id.row_ratingStars2));
    		holder.imgStar3 = ((ImageView)convertView.findViewById(R.id.row_ratingStars3));
    		holder.imgStar4 = ((ImageView)convertView.findViewById(R.id.row_ratingStars4));
    		holder.imgStar5 = ((ImageView)convertView.findViewById(R.id.row_ratingStars5));
    		
    		holder.imgStar6 = ((ImageView)convertView.findViewById(R.id.row1_ratingStars1));
    		holder.imgStar7 = ((ImageView)convertView.findViewById(R.id.row1_ratingStars2));
    		holder.imgStar8 = ((ImageView)convertView.findViewById(R.id.row1_ratingStars3));
    		holder.imgStar9 = ((ImageView)convertView.findViewById(R.id.row1_ratingStars4));
    		holder.imgStar10 = ((ImageView)convertView.findViewById(R.id.row1_ratingStars5));
    		
    		
    		
            convertView.setTag(holder);
        }
        else
        {
            holder = (EntryHolder)convertView.getTag();
        }
        
        double maxSiEMA = 0.0;
		for(int i=0;i<data.size();i++){
			SocialityDetails details=data.get(i);
			if(details.getmSiEMA()>maxSiEMA){
				maxSiEMA=details.getmSiEMA();
			}
		}
		
		double maxPropEMA = 0.0;
		for(int i=0;i<data.size();i++){
			SocialityDetails details=data.get(i);
			if(details.getmPropEMA()>maxPropEMA){
				maxPropEMA=details.getmPropEMA();
			}
		}

		
        SocialityDetails entry = data.get(position);

    	 
        if (!entry.getDevName().isEmpty() || !" ".equals(entry.getDevName())){
        	holder.txtTitle.setText(entry.getDevName());
        }
        else{
        	holder.txtTitle.setText("No name");
        }
       
          
        int stars = (int) (5*entry.getmSiEMA()/0.0564139626);
        
        Log.i(TAG, "stars in int " + stars);
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
		
		 int stars1 = (int) (5* entry.getmPropEMA()/3.45900878);
		
	    	Log.i(TAG, "stars1 in integer" + stars1);
			switch (stars1) {
			case 0:
				holder.imgStar6.setImageResource(R.drawable.no_star_rating_small);
				holder.imgStar7.setImageResource(R.drawable.no_star_rating_small);
				holder.imgStar8.setImageResource(R.drawable.no_star_rating_small);
				holder.imgStar9.setImageResource(R.drawable.no_star_rating_small);
				holder.imgStar10.setImageResource(R.drawable.no_star_rating_small);
				break;
			case 1:
				holder.imgStar6.setImageResource(R.drawable.star_rating_small);
				holder.imgStar7.setImageResource(R.drawable.no_star_rating_small);
				holder.imgStar8.setImageResource(R.drawable.no_star_rating_small);
				holder.imgStar9.setImageResource(R.drawable.no_star_rating_small);
				holder.imgStar10.setImageResource(R.drawable.no_star_rating_small);
				break;
			case 2:
				holder.imgStar6.setImageResource(R.drawable.star_rating_small);
				holder.imgStar7.setImageResource(R.drawable.star_rating_small);
				holder.imgStar8.setImageResource(R.drawable.no_star_rating_small);
				holder.imgStar9.setImageResource(R.drawable.no_star_rating_small);
				holder.imgStar10.setImageResource(R.drawable.no_star_rating_small);
				break;
			case 3:
				holder.imgStar6.setImageResource(R.drawable.star_rating_small);
				holder.imgStar7.setImageResource(R.drawable.star_rating_small);
				holder.imgStar8.setImageResource(R.drawable.star_rating_small);
				holder.imgStar9.setImageResource(R.drawable.no_star_rating_small);
				holder.imgStar10.setImageResource(R.drawable.no_star_rating_small);
				break;
			case 4:
				holder.imgStar6.setImageResource(R.drawable.star_rating_small);
				holder.imgStar7.setImageResource(R.drawable.star_rating_small);
				holder.imgStar8.setImageResource(R.drawable.star_rating_small);
				holder.imgStar9.setImageResource(R.drawable.star_rating_small);
				holder.imgStar10.setImageResource(R.drawable.no_star_rating_small);
				break;
			case 5:
				holder.imgStar6.setImageResource(R.drawable.star_rating_small);
				holder.imgStar7.setImageResource(R.drawable.star_rating_small);
				holder.imgStar8.setImageResource(R.drawable.star_rating_small);
				holder.imgStar9.setImageResource(R.drawable.star_rating_small);
				holder.imgStar10.setImageResource(R.drawable.star_rating_small);
				break;
			default:
				holder.imgStar6.setImageResource(R.drawable.no_star_rating_small);
				holder.imgStar7.setImageResource(R.drawable.no_star_rating_small);
				holder.imgStar8.setImageResource(R.drawable.no_star_rating_small);
				holder.imgStar9.setImageResource(R.drawable.no_star_rating_small);
				holder.imgStar10.setImageResource(R.drawable.no_star_rating_small);
			}
        
        return convertView;
    }
	
	/**
	 * This class holds the entry of the text and image view
	 *
	 */
	static class EntryHolder
    {
        
	
		TextView txtTitle; 
	    ImageView imgStar1;
	    ImageView imgStar2;
	    ImageView imgStar3;
	    ImageView imgStar4;
	    ImageView imgStar5;
	    
	    ImageView imgStar6;
	    ImageView imgStar7;
	    ImageView imgStar8;
	    ImageView imgStar9;
	    ImageView imgStar10;
    }
}
