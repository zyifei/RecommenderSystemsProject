package Yifei;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Database {
	
	public Database(){
		
	}

	static Map<Integer, Map<Integer, Integer>> userdata = new HashMap<Integer, Map<Integer, Integer>>();

	static Map<Integer, Map<Integer, Integer>> moviedata = new HashMap<Integer, Map<Integer, Integer>>();

	public void run() {

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			int user;
			int movie;
			int rating;

			// read data from csv file
			br = new BufferedReader(new FileReader("ratings.csv"));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] ratingdata = line.split(cvsSplitBy);

				// change String to integer
				user = Integer.parseInt(ratingdata[0]);
				movie = Integer.parseInt(ratingdata[1]);
				rating = Integer.parseInt(ratingdata[2]);

				// user--> movie-->ratings hashmap
				if (userdata.get(user) == null) {
					userdata.put(user, new HashMap<Integer, Integer>());
				}
				if (userdata.get(user).get(movie) == null) {
					userdata.get(user).put(movie, rating);
				}

				// movie-->user-->rating hashmap
				if (moviedata.get(movie) == null) {
					moviedata.put(movie, new HashMap<Integer, Integer>());
				}
				if (moviedata.get(movie).get(user) == null) {
					moviedata.get(movie).put(user, rating);
				}

			}

			// setting
			int userNum; // total number of user
			int movieNum; // total number of movie
			int ratingNum = 0; // total number of rating

			int rt1 = 0, rt2 = 0, rt3 = 0, rt4 = 0, rt5 = 0;
			double Umean = 0, UmedianValue = 0, Usd = 0, Umax = 0, Umin = 0;
			double UAVGmean = 0, UAVGmedianValue = 0, UAVGsd = 0, UAVGmax = 0, UAVGmin = 0;

			// get the user and movie tatal number
			userNum = userdata.size();
			movieNum = moviedata.size();

			// each loop go through one user, and get all movies ratings from
			// this user
			for (int userkey : userdata.keySet()) { // get each key(one user)
													// from userdata hashmap

				// System.out.println("userID: " + userkey);
				// System.out.print("movies' ratings: ");
				Map<Integer, Integer> userrating = new HashMap<Integer, Integer>();// create
																					// a
																					// new
																					// hashmap
				userrating = userdata.get(userkey);// each key point to a new
													// hashmap which store the
													// movie and rating

				// declare variable
				int ratings = 0;
				double sd = 0;
				// how many rating this user gave
				int ratingNumperUser;
				ratingNumperUser = userrating.size();
				// the total number of the rating
				ratingNum += ratingNumperUser;

				int[] arrayrate = new int[ratingNumperUser];
				int i = 0;
				int sumperuser = 0;// the sum of the rating number to calculate
									// mean later
				double mean;
				int max = 0;
				int min = 5;
				int r1 = 0, r2 = 0, r3 = 0, r4 = 0, r5 = 0;
				int medianValue = 0;

				// only get one rating once from this loop----one user, one
				// movie// the list of rating from one user after this loop
				for (int moviekey : userrating.keySet()) {
					ratings = userrating.get(moviekey);
					arrayrate[i] = ratings;
					i++;

					sumperuser += ratings; // get one rating each time, and add
											// them together
					// System.out.print(ratings);
					// max
					if (ratings > max) {
						max = ratings;
					}

					// min
					if (ratings < min) {
						min = ratings;
					}

					// Total number of ratings for each of the 5 ratings classes
					// per user
					if (ratings == 1) {
						r1++;
					} else if (ratings == 2) {
						r2++;
					} else if (ratings == 3) {
						r3++;
					} else if (ratings == 4) {
						r4++;
					} else if (ratings == 5) {
						r5++;
					}

				}

				// calculate the median of tone users ratings
				medianValue = median(ratingNumperUser, arrayrate);

				// calculate the mean rating per user
				mean = mean(sumperuser, ratingNumperUser);

				// calculate the sd rating per user
				sd = standard_deviation(ratingNumperUser, arrayrate, mean);

				// printing
				// System.out.println(" ");
				// System.out.println("mean: " + mean + ", " + "medianValue: " +
				// medianValue + ", " + "sd: " + sd + ", "
				// + "max: " + max + ", " + "min: " + min);
				// System.out.println(
				// "-------------------------------------------------------------------------------------");

				// average mean, medianvalue, sd, max, min
				Umean += mean;
				UmedianValue += medianValue;
				Usd += sd;
				Umax += max;
				Umin += min;

				// Total number of ratings for each of the 5 ratings classes
				rt1 += r1;
				rt2 += r2;
				rt3 += r3;
				rt4 += r4;
				rt5 += r5;

			}

			// the average number
			UAVGmean = Umean / userNum;
			UAVGmedianValue = UmedianValue / userNum;
			UAVGsd = Usd / userNum;
			UAVGmax = Umax / userNum;
			UAVGmin = Umin / userNum;

			// setting
			double Mmean = 0, MmedianValue = 0, Msd = 0, Mmax = 0, Mmin = 0;
			double MAVGmean = 0, MAVGmedianValue = 0, MAVGsd = 0, MAVGmax = 0, MAVGmin = 0;

			// each loop go through one movie, and get all users ratings from
			// this movie
			for (int moviekey : moviedata.keySet()) { // get each key(one user)
														// from moviedata
														// hashmap
				// System.out.println("movieID: " + moviekey);
				// System.out.print("users' ratings: ");
				Map<Integer, Integer> movierating = new HashMap<Integer, Integer>();// create
																					// a
																					// new
																					// hashmap
				movierating = moviedata.get(moviekey);// each key point to a new
														// hashmap which store
														// the movie and rating

				// declare variable
				int ratings = 0;
				double sd = 0;
				// how many rating this user gave
				int ratingNumperMovie;
				ratingNumperMovie = movierating.size();

				int[] arrayrate = new int[ratingNumperMovie];
				int i = 0;
				int sumpermovie = 0;// the sum of the rating number to calculate
									// mean later
				double mean;
				int max = 0;
				int min = 5;
				int medianValue = 0;

				// only get one rating once from this loop----one user, one
				// movie// the list of rating from one user after this loop
				for (int userkey : movierating.keySet()) {
					ratings = movierating.get(userkey);
					arrayrate[i] = ratings;
					i++;

					sumpermovie += ratings; // get one rating each time, and add
											// them together
					// System.out.print(ratings);
					// max
					if (ratings > max) {
						max = ratings;
					}

					// min
					if (ratings < min) {
						min = ratings;
					}

				}

				// calculate the median of tone users ratings
				medianValue = median(ratingNumperMovie, arrayrate);

				// calculate the mean rating per user
				mean = mean(sumpermovie, ratingNumperMovie);

				// calculate the sd rating per user
				sd = standard_deviation(ratingNumperMovie, arrayrate, mean);

				// printing
				// System.out.println(" ");
				// System.out.println("mean: " + mean + ", " + "medianValue: " +
				// medianValue + ", " + "sd: " + sd + ", "
				// + "max: " + max + ", " + "min: " + min);
				// System.out.println(
				// "-------------------------------------------------------------------------------------");

				Mmean += mean;
				MmedianValue += medianValue;
				Msd += sd;
				Mmax += max;
				Mmin += min;

			}

			// the average value
			MAVGmean = Mmean / movieNum;
			MAVGmedianValue = MmedianValue / movieNum;
			MAVGsd = Msd / movieNum;
			MAVGmax = Mmax / movieNum;
			MAVGmin = Mmin / movieNum;

			// ratings_density_metric
			int ratingt = rt1 + rt2 + rt3 + rt4 + rt5;
			double ratings_density_metric = ((double) ratingNum / (double) (userNum * movieNum));

			// printing
			// System.out.println("users average statistics");
			// System.out.println("AVGmean: " + UAVGmean + "\n" +
			// "AVGmedianValue: " + UAVGmedianValue + "\n" + "AVGsd: "
			// + UAVGsd + "\n" + "AVGmax: " + UAVGmax + "\n" + "AVGmin: " +
			// UAVGmin);
			// System.out.println(" ");
			// System.out.println("movies average statistics");
			// System.out.println("AVGmean: " + MAVGmean + "\n" +
			// "AVGmedianValue: " + MAVGmedianValue + "\n" + "AVGsd: "
			// + MAVGsd + "\n" + "AVGmax: " + MAVGmax + "\n" + "AVGmin: " +
			// MAVGmin);
			// System.out.println(" ");
			// System.out.println("-------------------------------------------------------------------------------------");
			// System.out.println("rating_class_1: " + rt1 + "\n" +
			// "rating_class_2: " + rt2 + "\n" + "rating_class_3: "
			// + rt3 + "\n" + "rating_class_4: " + rt4 + "\n" + "rating_class_5:
			// " + rt5 + "\n"
			// + "total_ratings_number: " + ratingt);
			// System.out.println(" ");
			// System.out.println("total_user_number: " + userNum + "\n" +
			// "total_movie_number: " + movieNum + "\n"
			// + "total_ratings_number: " + ratingNum);
			// System.out.println(" ");
			// System.out.println("ratings density metric: " +
			// ratings_density_metric);
			// System.out.println(" ");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// System.out.println("Done");

		// long endTime = System.currentTimeMillis();
		// long totalTime = endTime - startTime;
		//
		// System.out.println(totalTime + "ms");

		// long L1O_time = 0;
		// for (int t = 0; t <= 10; t++) {
		// L1O_time += L1O_time();
		// }
		//
		// L1O_time = L1O_time / 10;
		// System.out.println("the average runtime for 1 complete L10
		// test-cycle: " + L1O_time + "ms");

	}

	// ------------week 1 function-------------------
	public static double mean(double sum, double number) {
		double mean;
		mean = sum / number;
		return mean;
	}

	public static int meanint(int sum, int number) {
		int mean;
		mean = sum / number;
		return mean;
	}

	public int median(int ratingNumperUser, int[] arrayrate) {
		Arrays.sort(arrayrate);
		int middle = ratingNumperUser / 2;
		int medianValue = 0;
		if (ratingNumperUser % 2 == 1) {
			medianValue = arrayrate[middle];
		} else {
			medianValue = (arrayrate[middle - 1] + arrayrate[middle]) / 2;
		}
		return medianValue;
	}

	public double standard_deviation(int ratingNumperUser, int[] arrayrate, double mean) {
		double sd = 0;
		for (int j = 0; j < ratingNumperUser; j++) {
			sd = sd + Math.pow(arrayrate[j] - mean, 2);
		}

		double std = Math.sqrt(sd / ratingNumperUser);

		return std;

	}

	public void database() {

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			int user;
			int movie;
			int rating;

			// read data from csv file
			br = new BufferedReader(new FileReader("ratings.csv"));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] ratingdata = line.split(cvsSplitBy);

				// change String to integer
				user = Integer.parseInt(ratingdata[0]);
				movie = Integer.parseInt(ratingdata[1]);
				rating = Integer.parseInt(ratingdata[2]);

				// user--> movie-->ratings hashmap
				if (userdata.get(user) == null) {
					userdata.put(user, new HashMap<Integer, Integer>());
				}
				if (userdata.get(user).get(movie) == null) {
					userdata.get(user).put(movie, rating);
				}

				// movie-->user-->rating hashmap
				if (moviedata.get(movie) == null) {
					moviedata.put(movie, new HashMap<Integer, Integer>());
				}
				if (moviedata.get(movie).get(user) == null) {
					moviedata.get(movie).put(user, rating);
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
