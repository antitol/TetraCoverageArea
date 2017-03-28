package tetracoveragearea.common;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;

/**
 * Created by anatoliy on 27.02.17.
 */
public abstract class SentenceAdapter implements SentenceListener {
    @Override
    public void readingPaused() {}

    @Override
    public void readingStarted() {}

    @Override
    public void readingStopped() {}

    @Override
    public void sentenceRead(SentenceEvent event) {}
}
