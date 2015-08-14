/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import Dataset.DataSet;
import Evaluation.Evaluation;

/**
 *
 * @author daq
 */
public class TestAss4 {

    public static void main(String[] args) {
        // for RandomForest
        System.out.println("for RandomForest");

        String[] dataPaths = new String[]{"/home/geyanhao801/Documents/HadoopForest2/src/main/resources/traindata0804(DWXZDM).csv",
                "/home/geyanhao801/Documents/HadoopForest2/src/main/resources/traindata0804 (ZWRYLB).csv"};
        for (String path : dataPaths) {
            DataSet dataset = new DataSet(path);

            // conduct 10-cv 
            Evaluation eva = new Evaluation(dataset, "RandomForest");
            eva.crossValidation();

            // print mean and standard deviation of accuracy
            System.out.println("Dataset:" + path + ", mean and standard deviation of accuracy:" + eva.getAccMean() + "," + eva.getAccStd());
        }


    }
}
