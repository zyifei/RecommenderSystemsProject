package Yifei;


public class RecSys {

	public static void main(String[] args){
		
		Database database = new Database();
		
		database.database();

		

		Resnicks_Formula res = new Resnicks_Formula();
		
		res.cos_sim_user_map();
		
		res.generateCsvFile_res_prediction("res_prediction_2", 2);
		System.out.println(res.L1O_time(2));
		System.out.println();
		
		res.generateCsvFile_res_prediction("res_prediction_3", 3);
		System.out.println(res.L1O_time(3));
		System.out.println();
		
		res.generateCsvFile_res_prediction("res_prediction_4", 4);
		System.out.println(res.L1O_time(4));
		System.out.println();
		
		res.generateCsvFile_res_prediction("res_prediction_5", 5);
		System.out.println(res.L1O_time(5));
		System.out.println();
		
		res.generateCsvFile_res_prediction("res_prediction_6", 6);
		System.out.println(res.L1O_time(6));
		System.out.println();
		
		res.generateCsvFile_res_prediction("res_prediction_7", 7);
		System.out.println(res.L1O_time(7));
		System.out.println();
		
		res.generateCsvFile_res_prediction("res_prediction_8", 8);
		System.out.println(res.L1O_time(8));
		System.out.println();
		
		res.generateCsvFile_res_prediction("res_prediction_9", 9);
		System.out.println(res.L1O_time(9));
		System.out.println();
		
		res.generateCsvFile_res_prediction("res_prediction_10", 10);
		System.out.println(res.L1O_time(10));
		System.out.println();
		
		res.generateCsvFile_res_prediction("res_prediction_20", 20);
		System.out.println(res.L1O_time(20));
		System.out.println();
		
		res.generateCsvFile_res_prediction("res_prediction_30", 30);
		System.out.println(res.L1O_time(30));
		System.out.println();

	
	}





}