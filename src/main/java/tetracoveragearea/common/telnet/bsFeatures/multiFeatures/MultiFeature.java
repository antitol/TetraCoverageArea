package tetracoveragearea.common.telnet.bsFeatures.multiFeatures;


import tetracoveragearea.common.telnet.bsFeatures.lineFeatures.BSFeature;
import tetracoveragearea.common.telnet.bsMessages.BSMessageParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anatoliy on 18.05.17.
 */
public abstract class MultiFeature extends BSFeature<List<? extends BSFeature>>  {

    private List<String> sentences = new ArrayList<>();

    public MultiFeature(List<String> strings) {
        this.sentences = new ArrayList<>(strings);
    }

    public MultiFeature() {
    }

    public void parse() throws BSMessageParseException {

        int position = 1;

        for (int i = 0; i < getFeature().size(); i++) {
            BSFeature feature = getFeature().get(i);
             if (feature.getLength() == 1) {
                 feature.parse(sentences.get(position));
             } else {
                 ((MultiFeature) feature).setSentences(sentences.subList(position, position + feature.getLength()));
                 ((MultiFeature) feature).parse();
             }

             position += feature.getLength();
        }
    }

    public void setSentences(List<String> sentences) {
        this.sentences = sentences;
    }

    public List<String> getSentences() {
        return sentences;
    }
}
