package Yifei;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Distance_based_Prediction {

	public Distance_based_Prediction(){
		
	}
	
	//the function used to calculate the similarity for each two users.
	public static double sim(int user_id, int sim_user_id) {

		double i = 0;
		double diff = 0;
		int umr = 0;
		int sim_umr = 0;
		double sim = 0;
		double similarity = 0;

		Map<Integer, Integer> userrating = new HashMap<Integer, Integer>();

		Map<Integer, Integer> sim_userrating = new HashMap<Integer, Integer>();

		userrating = Database.userdata.get(user_id);
		sim_userrating = Database.userdata.get(sim_user_id);
		if (userrating != null) {
			for (int moviekey : userrating.keySet()) {

				if (sim_userrating.containsKey(moviekey)) {
					umr = userrating.get(moviekey);
					sim_umr = sim_userrating.get(moviekey);
					diff += Math.pow(umr - sim_umr, 2);
					i++;
				}

			}
		}

		sim = diff / i;
		similarity = 1 - (sim / 16);

		return similarity;

	}
	
	//the function used to calculate the common movies number for each two user
	public static int common_muvie(int user, int sim_user) {
		int common_movie = 0;
		for (int movie : Database.userdata.get(user).keySet()) {
			if (Database.userdata.get(sim_user).containsKey(movie)) {
				common_movie++;
			}
		}

		return common_movie;
	}

	
	static Map<Integer, Map<Double, Integer>> sim_user_data = new HashMap<Integer, Map<Double, Integer>>();

	//the function used to produce the similarity metric
	public static Map<Integer, Map<Double, Integer>> sim_user_map() {
		double sim = 0;
		for (int userkey : Database.userdata.keySet()) {
			for (int sim_user : Database.userdata.keySet()) {
				if (userkey != sim_user) {
					// only common movie bigger than a set number , we calculate
					// the sim
					if (common_muvie(userkey, sim_user) >= 7) {
						sim = sim(userkey, sim_user);
						if (sim_user_data.get(userkey) == null) {
							sim_user_data.put(userkey, new TreeMap<Double, Integer>());
						}
						if (sim_user_data.get(userkey).get(sim) == null) {
							sim_user_data.get(userkey).put(sim, sim_user);
						}

					}
				}
			}

		}
		return sim_user_data;
	}
	
	
/*
 * 
 *  double result = Similarity.getCosineSim(User1, User2)
 *  Similarity.getDistance(User1, User2)
 * 
 * 
 */
	

	//the function used to make a prediction for each user movie pair
	public static  double prediction(int user_id, int item_id, int size) {
		double prediction = 0;
		Map<Double, Integer> sim_user_list = new TreeMap<Double, Integer>();

		sim_user_list = sim_user_data.get(user_id);

		int s = 0;
		int sim_user = 0;
		double w_rating = 0;
		int sim_rating = 0;
		double weight = 0;

		Map<Integer, Integer> sim_user_rating = new HashMap<Integer, Integer>();
		
		if (sim_user_list != null) {
			
			ArrayList<Double> keys = new ArrayList<Double>(sim_user_list.keySet());
			for (int i = keys.size() - 1; i >= 0; i--) {

				sim_user = sim_user_list.get(keys.get(i)); 

				sim_user_rating = Database.userdata.get(sim_user); 

				for (int sim_user_item : sim_user_rating.keySet()) {
					
					if (item_id == sim_user_item) { 
					
						sim_rating = sim_user_rating.get(item_id); 
						
						w_rating += sim_rating * keys.get(i); 
						
						weight += keys.get(i); 
						
						s++;
						
						if (s == size) {
							break;
						}

					} else {
						w_rating += 0;
					}
				}
				if (s == size) {
					break;
				}

			}
		}

		prediction = w_rating / weight;

		return prediction;
	}

	//the function used to get the csv file for each neighborhood size
	public  void generateCsvFile_distance_based(String sFileName, int size) {
		try {
			FileWriter writer = new FileWriter(sFileName);

			int cannot_p = 0;
			double coverage = 0;
			double coverage1;
			double totalE = 0;
			double mean_RMSE = 0;
			double p = 0;
			int r;
			double E = 0;
			double RMSE = 0;

			for (int moviekey : Database.moviedata.keySet()) {
				Map<Integer, Integer> movierating = new HashMap<Integer, Integer>();

				movierating = Database.moviedata.get(moviekey);

				for (int userkey : movierating.keySet()) {

					r = movierating.get(userkey);

					// call mean_item_rating method
					p = prediction(userkey, moviekey, size);

					// calculate the total RMSE

					if (!Double.isNaN(p)) {
						E = Math.pow(p - r, 2);
						RMSE = Math.sqrt(E);

						totalE += RMSE;

						writer.append(Integer.toString(userkey));
						writer.append(',');
						writer.append(Integer.toString(moviekey));
						writer.append(',');
						writer.append(Integer.toString(r));
						writer.append(',');
						writer.append(Double.toString(p));
						writer.append(',');
						writer.append(Double.toString(RMSE));
						writer.append('\n');

					} else {

						writer.append(Integer.toString(userkey));
						writer.append(',');
						writer.append(Integer.toString(moviekey));
						writer.append(',');
						writer.append(Integer.toString(r));
						writer.append(',');
						writer.append("null");
						writer.append(',');
						writer.append("null");
						writer.append('\n');
						cannot_p++;

					}

				}
			}

			// calculate the coverage data
			coverage1 = (100000 - cannot_p);
			
			coverage = coverage1 / 100000;
			

			// calculate the average of overall RMES data
			mean_RMSE = totalE / coverage1;
			
			System.out.println("size"+ size);
			System.out.println("the number of data set can not be predicated:  " + cannot_p);
			System.out.println("coverage: " + coverage);
			System.out.println("the average RMSE:  " + mean_RMSE);
			System.out.println("------------------------");
			

			writer.append("coverage");
			writer.append(',');
			writer.append(Double.toString(coverage));
			writer.append(',');
			writer.append("   ");
			writer.append(',');
			writer.append("   ");
			writer.append(',');
			writer.append("   ");
			writer.append('\n');

			writer.append("mean RMSE");
			writer.append(',');
			writer.append(Double.toString(mean_RMSE));
			writer.append(',');
			writer.append("   ");
			writer.append(',');
			writer.append("   ");
			writer.append(',');
			writer.append("   ");
			writer.append('\n');

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//the function used to calculate the time per times
	public  long L1O_TIME(int size) {

		
		long startTimeL1O = System.currentTimeMillis();
			int cannot_p = 0;
			double coverage = 0;
			double coverage1;
			double totalE = 0;
			double mean_RMSE = 0;
			double p = 0;
			int r;
			double E = 0;
			double RMSE = 0;

			for (int moviekey : Database.moviedata.keySet()) {
				Map<Integer, Integer> movierating = new HashMap<Integer, Integer>();

				movierating = Database.moviedata.get(moviekey);

				for (int userkey : movierating.keySet()) {

					r = movierating.get(userkey);

					// call mean_item_rating method
					p = prediction(userkey, moviekey, size);

					// calculate the total RMSE

					if (!Double.isNaN(p)) {
						E = Math.pow(p - r, 2);
						RMSE = Math.sqrt(E);

						totalE += RMSE;

						

					} else {

						
						cannot_p++;

					}

				}
			}

			// calculate the coverage data
			coverage1 = (100000 - cannot_p);
			
			coverage = coverage1 / 100000;
			

			// calculate the average of overall RMES data
			mean_RMSE = totalE / coverage1;
		
			
			long endTimeL1O = System.currentTimeMillis();
			long totalTimeL1O = endTimeL1O - startTimeL1O;

			return totalTimeL1O;
	}
	
	
	//the function used to calculate the average time (10 times)
	public long L1O_TIME_AVE(int size){
		long time = 0;
		
		for(int i = 0; i<size; i++){
			time += L1O_TIME(size);
		}
		
		time = time/size;
		
		return time;
	}
	
}
