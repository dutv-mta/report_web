package web.social.facebook.analyze;

import vn.com.datasection.segment.DSVNSegmenter;
import vn.com.datasection.segment.Segmenter;
import vn.com.datasection.utils.DataType;

import java.util.*;

public class Segment {
    public static List<WordRank> extractKeys(List<String> contents) {
        Map<String, Integer> mapWords = new HashMap<>();
        Segmenter segment = new DSVNSegmenter(DataType.SEG_SNS_KO_DINHTU);

        for (String content : contents) {
            String segmented = segment.segment(content).toLowerCase();
            String words[] = segmented.split(" ");
            for (String word : words) {
                if (word.length() > 5 || word.contains("_")) {// lựa chọn những từ ghép or từ dài
                    int count = 1;
                    if (mapWords.containsKey(word)) {
                        count += mapWords.get(word);
                    }
                    mapWords.put(word, count);
                }
            }
        }

        TreeMap<String, Integer> trees = new TreeMap<>(new ValueComparator<>(mapWords));
        trees.putAll(mapWords);

        int maxWord = 20;
        List<WordRank> words = new ArrayList<>();
        for (Map.Entry<String, Integer> tree : trees.entrySet()) {
            words.add(new WordRank(tree.getKey(), tree.getValue()));
            if (words.size() == maxWord) {
                break;
            }
        }
        return words;
    }

    public static void main(String[] args) {
        List<String> cnts = new ArrayList<>();
        cnts.add(
                "Lãnh đạo CLB TP.HCM xác nhận Á quân của V-League 2019 đang tích cực đàm phán để đưa tiền đạo Nguyễn Công Phượng trở lại Việt Nam thi đấu.");
        cnts.add(
                "Thời gian vừa qua, CLB TP.HCM liên tục gây xôn xao làng bóng Việt Nam với những bản hợp đồng chất lượng nhằm chuẩn bị cho mùa giải 2020. Bởi ngoài việc tham dự V-League, Cúp Quốc gia thì CLB TP.HCM còn đại diện cho bóng đá Việt Nam tham dự vòng sơ loại AFC Champions League 2020.");
        List<WordRank> words = extractKeys(cnts);
        for (WordRank word : words) {
            System.out.println(word.getWord() + "---" + word.getCount());
        }
    }
}
