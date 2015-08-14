package RandomForest;
import java.util.*;

/**
 * 
 *  Random Forest
 */

public class RandomForest extends Classifier {
    // 存储可放回抽取生成的新样本集
    public class RepickSamples {
        double[][] features;
        double[] labels;
        int[] index;

        public double[][] getFeatures(){
            return features;
        }

        public double[] getLabels(){
            return labels;
        }
    }

    private static int classifier = 130; // 生成分类器的数量
    private RandomDecisionTree forest[];

    public RandomForest() {
    }

    @Override
    public void train(boolean[] isCategory, double[][] features, double[] labels) {
        forest = new RandomDecisionTree[classifier];
        for (int i = 0; i < classifier; ++i) {
            RepickSamples samples = repickSamples(features, labels);
            forest[i] = new RandomDecisionTree();
            forest[i].train(isCategory, samples.features, samples.labels);

        }
        System.out.println( classifier+"SubDecision Trees Have Builded");

    }

    // 可放回收取新样本集
    public RepickSamples repickSamples(double[][] features, double[] labels) {
        RepickSamples samples = new RepickSamples();
        int size = labels.length;
        Random random = new Random();

        samples.features = new double[size][];
        samples.labels = new double[size];
        samples.index = new int[size];
        for (int i = 0; i < size; ++i) {
            int index = random.nextInt(size);
            samples.features[i] = features[index].clone();
            samples.labels[i] = labels[index];
            samples.index[i] = index;
        }
        //System.out.println("Samples Have bean Repicked");

        return samples;
    }

    @Override
    public double predict(double[] features) {
        HashMap<Double, Integer> counter = new HashMap<Double, Integer>();
        for (int i = 0; i < forest.length; ++i) {
            double label = forest[i].predict(features);
            if (counter.get(label) == null) {
                counter.put(label, 1);
            } else {
                int count = counter.get(label) + 1;
                counter.put(label, count);
            }
        }

        int temp_max = 0;
        double label = 0;
        Iterator<Double> iterator = counter.keySet().iterator();
        while (iterator.hasNext()) {
            double key = iterator.next();
            int count = counter.get(key);
            if (count > temp_max) {
                temp_max = count;
                label = key;
            }
        }
        //System.out.println("Samples Have bean Predicted");

        return label;
    }
}

// <<<<--------------------------华丽的分界线，下面是随机决策树的实现---------------------------->>>>

