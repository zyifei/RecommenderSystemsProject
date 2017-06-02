package Yifei;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Cosine_based_Prediction {
	
	public Cosine_based_Prediction(){
		
	}

	//calculate the similarity between two user
	public static double cosine_sim(int user_id, int sim_user_id){
		
		double sim = 0;
//		
//		double temp1 = 0;
//		double temp2 = 0;
//		double temp3 = 0;
//		double temp4 = 0;
//		double temp5 = 0;
//		double temp6 = 0;
//		double temp7 = 0;
//		double temp8 = 0;
		double temp9 = 0;
		double temp10 = 0;
		double temp11 = 0;
		double temp12 = 0;
		double temp13 = 0;
		double temp14 = 0;
		double temp15 = 0;
		double temp16 = 0;
		
		Map<Integer, Integer> userrating = new HashMap<Integer, Integer>();

		Map<Integer, Integer> sim_userrating = new HashMap<Integer, Integer>();
		
		userrating = Database.userdata.get(user_id);
		sim_userrating = Database.userdata.get(sim_user_id);
		

		double mean_rating_user = mean_rating(user_id);
		
		double mean_rating_sim_user = mean_rating(sim_user_id);
		
		
		if (userrating != null) {
			//System.out.println("not null");
			for (int moviekey : userrating.keySet()) {
				
				if (sim_userrating.containsKey(moviekey)) {
//					System.out.println("-------------------------------------");
//					System.out.println("contain key");
//					
					//temp1 = (userrating.get(moviekey) - mean_rating_user); 
					temp9 = userrating.get(moviekey);
					//System.out.println("1   "+temp1);
					//temp2 = (sim_userrating.get(moviekey) - mean_rating_sim_user);
					temp10 = sim_userrating.get(moviekey);
					//System.out.println(sim_userrating.get(moviekey));
					//System.out.println(mean_rating_sim_user);
					
					//System.out.println("2   "+temp2);
					//temp3 += temp1*temp2;
					temp11 += temp9*temp10;
					
					//System.out.println("3   "+temp3);
					//temp4 += Math.pow( temp1, 2);
					temp12 += Math.pow( temp9, 2); 
					//System.out.println("4   "+temp4);
					//temp5 += Math.pow(temp2 , 2);
					temp13 += Math.pow( temp10, 2); 
					//System.out.println("5   "+temp5);
				}
			}
		}
//		if (temp5 == 0) {
			temp14 = Math.sqrt(temp12);

			temp15 = Math.sqrt(temp13);

			temp16 = temp14 * temp15;

			sim = temp11 / temp16;

			return sim;
//		} else {
//			temp6 = Math.sqrt(temp4);
//
//			temp7 = Math.sqrt(temp5);
//
//			temp8 = temp6 * temp7;
//
//			sim = temp3 / temp8;
//
//			return sim;
		//}
	}
	
	//calculate the mean rating
	public static double mean_rating(int user_id){
		double mean = 0;
		double rating = 0;
		double i = 0;
		Map<Integer, Integer> userrating = new HashMap<Integer, Integer>();
		userrating = Database.userdata.get(user_id);
		for (int moviekey : userrating.keySet()){
			rating += userrating.get(moviekey);
			i++;
		}
		
		mean = rating/i;
		
		
		return mean;
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
	
	
	static Map<Integer, Map<Double, Integer>> cos_sim_user_data = new HashMap<Integer, Map<Double, Integer>>();

	//the function used to produce the similarity metric
	public static Map<Integer, Map<Double, Integer>> cos_sim_user_map() {

		double sim = 0;
		for (int userkey : Database.userdata.keySet()) {
			for (int sim_user : Database.userdata.keySet()) {
				if (userkey != sim_user) {
					// only common movie bigger than a set number , we calculate
					// the sim
					if (common_muvie(userkey, sim_user) >= 7) {
						sim = cosine_sim(userkey, sim_user);
						
						
						if (cos_sim_user_data.get(userkey) == null) {
							cos_sim_user_data.put(userkey, new TreeMap<Double, Integer>());
						}
						if (cos_sim_user_data.get(userkey).get(sim) == null) {
							cos_sim_user_data.get(userkey).put(sim, sim_user);
						}

					}
				}
			}

		}
		return cos_sim_user_data;
	}
	
	
	public static Map<Integer, Map<Double, Integer>> test_cos_sim_user_map() {

		double sim = 0;
		int userkey = 15;  //userid = 1
		
		int i =0,j=0;
			for (int sim_user : Database.userdata.keySet()) {
				if (userkey != sim_user) {
					// only common movie bigger than a set number , we calculate
					// the sim
					if (common_muvie(userkey, sim_user) >= 7) {
						sim = cosine_sim(userkey, sim_user);

						i++;
						if (cos_sim_user_data.get(userkey) == null) {
							cos_sim_user_data.put(userkey, new TreeMap<Double, Integer>());
						}
						if (cos_sim_user_data.get(userkey).get(sim) == null) {
							cos_sim_user_data.get(userkey).put(sim, sim_user);
							j++;
						}

					}
				}
			

		}

		return cos_sim_user_data;
	}
	
	
	//the function used to make a prediction for each user movie pair
		public static  double prediction(int user_id, int item_id, int size) {
			double prediction = 0;
			Map<Double, Integer> sim_user_list = new TreeMap<Double, Integer>();

			sim_user_list = cos_sim_user_data.get(user_id);
			

			int s = 0;
			int sim_user = 0;
			double w_rating = 0;
			int sim_rating = 0;
			double weight = 0;

			Map<Integer, Integer> sim_user_rating = new HashMap<Integer, Integer>();
			//System.out.println("hello! there! pay attention!");
			if (sim_user_list != null) {
				
				ArrayList<Double> keys = new ArrayList<Double>(sim_user_list.keySet());
				for (int i = keys.size() - 1; i >= 0; i--) {

					sim_user = sim_user_list.get(keys.get(i)); 
//					System.out.print("-----------------------------------------------------");
//					System.out.print(sim_user);
//					System.out.print("     ");
//					System.out.println(keys.get(i));
					sim_user_rating = Database.userdata.get(sim_user); 
					
					if (!Double.isNaN(keys.get(i))) {
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
		
		
		public  long L1O_time(int size) {
			
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
	
	
	
}
