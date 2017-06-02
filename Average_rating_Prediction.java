package Yifei;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Average_rating_Prediction {

	public Average_rating_Prediction(){
		
	}

	public static double mean_item_rating(int item_id, int user_id) {
		double mean = 0;

		// declare variable
		int ratings = 0;
		double ratingNumperMovie = 0;
		int testset = 0;
		double sumtrain = 0;

		for (int moviekey : Database.moviedata.keySet()) {
			// search the item_id
			if (item_id == moviekey) {
				// build the new hashmap to store all user ratings of the
				// item_id
				Map<Integer, Integer> trainset = new HashMap<Integer, Integer>();
				trainset = Database.moviedata.get(moviekey);
				// the number of ratings that we need to predicate
				ratingNumperMovie = (trainset.size()) - 1;

				// add all ratings of this item together
				for (int userkey : trainset.keySet()) {
					// training aet
					if (userkey != user_id) {
						ratings = trainset.get(userkey);
						sumtrain += ratings;
					} else {
						// test set
						testset = ratings;
					}

				}
			}
		}
		// calculate the mean rating per user
		mean = Database.mean(sumtrain, ratingNumperMovie);

		return mean;
	}

	public void L1O() {

		int cannot_p = 0;

		for (int moviekey : Database.moviedata.keySet()) {
			Map<Integer, Integer> movierating = new HashMap<Integer, Integer>();

			movierating = Database.moviedata.get(moviekey);

			int N = movierating.size();
			double p = 0;
			int r;
			double E = 0;
			double RMSE = 0;

			for (int userkey : movierating.keySet()) {

				r = movierating.get(userkey);

				if (N != 1) {
					// call mean_item_rating method
					p = mean_item_rating(moviekey, userkey);
					// calculate the total RMSE
					E = Math.pow(p - r, 2);
					RMSE = Math.sqrt(E);
				} else {
					cannot_p++;
				}
			}
		}
	}

	// Create a csv data-file from the results of this L1O test. This csv should
	// elements: user_id, item_id, actual rating, predicted rating, RMSE.

	public  void generateCsvFile(String sFileName) {
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

				int N = movierating.size();

				for (int userkey : movierating.keySet()) {

					r = movierating.get(userkey);

					if (N != 1) {

						// call mean_item_rating method
						p = mean_item_rating(moviekey, userkey);

						// calculate the total RMSE
						E = Math.pow(p - r, 2);
						RMSE = Math.sqrt(E);
						if (RMSE != 0) {
							totalE += RMSE;
						}
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
			System.out.println("the number of data set can not be predicated:  " + cannot_p);
			coverage = coverage1 / 100000;
			System.out.println("coverage: " + coverage);

			// calculate the average of overall RMES data
			mean_RMSE = totalE / coverage1;
			System.out.println("the average RMSE:  " +  mean_RMSE);

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

	// calculate the average runtime for 1 complete L10 test-cycle
	public long L1O_time() {

		long startTimeL1O = System.currentTimeMillis();
		int cannot_p = 0;

		for (int moviekey : Database.moviedata.keySet()) {
			Map<Integer, Integer> movierating = new HashMap<Integer, Integer>();

			movierating = Database.moviedata.get(moviekey);

			int N = movierating.size();
			double p = 0;
			int r;
			double E = 0;
			double RMSE = 0;

			for (int userkey : movierating.keySet()) {

				r = movierating.get(userkey);

				if (N != 1) {

					// call mean_item_rating method
					p = mean_item_rating(moviekey, userkey);

					// calculate the total RMSE
					E = Math.pow(p - r, 2);
					RMSE = Math.sqrt(E);

				} else {

					cannot_p++;
				}

			}
		}
		long endTimeL1O = System.currentTimeMillis();
		long totalTimeL1O = endTimeL1O - startTimeL1O;

		return totalTimeL1O;

	}
}
