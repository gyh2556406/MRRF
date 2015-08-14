package TrainingMapRed;

import Dataset.DataSet;
import RandomForest.*;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Evaluation.Evaluation;
import java.io.IOException;
import java.sql.DriverPropertyInfo;
import java.util.ArrayList;

/**
 * Created by geyanhao801 on 8/13/15.
 */
public class TrainningMR extends Configured implements Tool{
    public static class TraningMapper extends Mapper<IntWritable,Text,Text,DoubleWritable> {
        private static final Logger log = LoggerFactory.getLogger(TraningMapper.class);
        private RandomDecisionTree forestTree[];
        private RandomForest forest;
        private RandomForest.RepickSamples samples ;
        private boolean[] isCategory;
        private double[][] features ;
        private double[] labels;
        /**
         * Load the Training data and Bagging
         */
        protected void setup(Context context) throws IOException,InterruptedException{
            super.setup(context);
            Configuration conf = context.getConfiguration();
            String path = conf.get("path");
            String nTreeString = conf.get("nTrees");
            int nTrees = Integer.parseInt(nTreeString);
            log.info("Loading the data...");
            DataSet dataset = new DataSet(path);
            log.info("Data loaded : {} instances", dataset.getNumInstnaces());
            Evaluation eva = new Evaluation(dataset, "RandomForest");
            ArrayList<Object> devidedDataSet = eva.devideDataSet();
            features = (double[][])devidedDataSet.get(0);
            labels = (double[])devidedDataSet.get(1);
            isCategory = (boolean[])devidedDataSet.get(4);
            forest.train(isCategory,features,labels);
        }

        protected void map(IntWritable key,Text value,Context context) throws IOException,InterruptedException{
            //use mapper to predict instance;key == input attrbutes; value == realLabel
            String[] valueSplit = StringUtils.splitPreserveAllTokens(value.toString(),",");
            double[] feature = new double[valueSplit.length-1];
            double realLabel = Double.parseDouble(valueSplit[valueSplit.length-1]);

            if(valueSplit.length != features[0].length)
                context.write(null, null);

            for(int i = 0 ;i<valueSplit.length-1;i++){
                feature[i] = Double.parseDouble(valueSplit[i]);
            }

            double predictLabel = forest.predict(feature);

            context.write(key, forestMapTree);
        }

    }

}
