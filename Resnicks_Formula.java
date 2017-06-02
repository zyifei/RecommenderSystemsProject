package Yifei;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Resnicks_Formula {
	
	private static final double NaN = 0;

	public Resnicks_Formula(){
		
	}
	
	//calculate the similarity between two user
		public static double cosine_sim(int user_id, int sim_user_id){
			
			double sim = 0;
			
			double temp1 = 0;
			double temp2 = 0;
			double temp3 = 0;
			double temp4 = 0;
			double temp5 = 0;
			double temp6 = 0;
			double temp7 = 0;
			double temp8 = 0;

			
			Map<Integer, Integer> userrating = new HashMap<Integer, Integer>();

			Map<Integer, Integer> sim_userrating = new HashMap<Integer, Integer>();
			
			userrating = Database.userdata.get(user_id);
			sim_userrating = Database.userdata.get(sim_user_id);
			

			double mean_rating_user = mean_rating(user_id);
			
			double mean_rating_sim_user = mean_rating(sim_user_id);
			
			
			if (userrating != null) {
				for (int moviekey : userrating.keySet()) {
					
					if (sim_userrating.containsKey(moviekey)) {

					
						temp1 = (userrating.get(moviekey) - mean_rating_user); 
						temp2 = (sim_userrating.get(moviekey) - mean_rating_sim_user);
						temp3 += temp1*temp2;
						temp4 += Math.pow( temp1, 2);
						temp5 += Math.pow(temp2 , 2);

					}
				}
			}


				temp6 = Math.sqrt(temp4);

				temp7 = Math.sqrt(temp5);

				temp8 = temp6 * temp7;

				sim = temp3 / temp8;

				return sim;
			
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
//						System.out.print("-----------------------------------------------------");
//						System.out.print(sim_user);
//						System.out.print("     ");
//						System.out.println(keys.get(i));
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
			
			


	// the function used to make a prediction for each user movie pair
	public static  double prediction_Resnicks_Formula(int user_id, int item_id, int size) {
					double prediction = 0;
					Map<Double, Integer> sim_user_list = new TreeMap<Double, Integer>();

					sim_user_list = cos_sim_user_data.get(user_id);
					

					int s = 0;
					int sim_user = 0;
					double w_rating = 0;
					int sim_rating = 0;
					double weight = 0;
					double sim_user_rating_mean = 0;
					double prediction_part = 0;
					double user_maen_rating = 0;
					
					Map<Integer, Integer> sim_user_rating = new HashMap<Integer, Integer>();
					//System.out.println("hello! there! pay attention!");
					if (sim_user_list != null) {
						
						ArrayList<Double> keys = new ArrayList<Double>(sim_user_list.keySet());
						for (int i = keys.size() - 1; i >= 0; i--) {

							
							sim_user = sim_user_list.get(keys.get(i)); //get the user id of the top similar users

							sim_user_rating = Database.userdata.get(sim_user); //get the rating_movie map for this sim user key is item, value is rating
							
							sim_user_rating_mean = mean_rating(sim_user);//get the mean rating of the sim_users all ratings
							
							if (!Double.isNaN(keys.get(i))) {
							for (int sim_user_item : sim_user_rating.keySet()) {
								
								if (item_id == sim_user_item) {  //only make a prediction base on the user which has this movie
									
									if(sim_user_rating.containsKey(item_id)){
									
										sim_rating = sim_user_rating.get(item_id); 
									
									w_rating += (sim_rating-sim_user_rating_mean) * keys.get(i); 
									
									weight += keys.get(i); 
									s++;

									if (s == size) {
										break;
									}
							

									}
								}
							}
							if (s == size) {
								break;
							}
							}
							
						}
					}
					
					user_maen_rating = mean_rating(user_id);
					prediction_part =  w_rating / weight;

					prediction = user_maen_rating + prediction_part;
					return prediction;		
				}
			
	
			//the function used to get the csv file for each neighborhood size
			public  void generateCsvFile_res_prediction(String sFileName, int size) {
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
							p = prediction_Resnicks_Formula(userkey, moviekey, size);

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
							p = prediction_Resnicks_Formula(userkey, moviekey, size);

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
