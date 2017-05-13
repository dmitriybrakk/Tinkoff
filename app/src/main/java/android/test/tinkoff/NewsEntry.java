package android.test.tinkoff;

import android.os.Build;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by dmitriy on 5/12/17.
 */

public class NewsEntry {

    private String id;
    private String text;
    private PublicationDate publicationDate;

    public String getId(){return id;}
    public String getText(){return text;}
    public PublicationDate getPublicationDate(){return publicationDate;}


    @Override
    public String toString(){
        return String.valueOf(publicationDate.getDate());
    }

    public class PublicationDate {
        private long milliseconds;

        public long getDate(){
            return milliseconds;
        }
    }

    public static class DateComparator implements Comparator<NewsEntry> {
        @Override
        public int compare(NewsEntry e1, NewsEntry e2){
            long millis1 = e1.getPublicationDate().getDate();
            long millis2 = e2.getPublicationDate().getDate();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return Long.compare(millis2, millis1);
            } else {
                if (millis1 < millis2){return -1;}
                else if (millis1 > millis2){return 1;}
                return 0;
            }
        }
    }
}
