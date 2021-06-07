import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.v1.Books;
import com.google.api.services.books.v1.BooksRequestInitializer;
import com.google.api.services.books.v1.Books.Volumes.List;
import com.google.api.services.books.v1.model.Volume;
import com.google.api.services.books.v1.model.Volumes;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;

public class Book {

    static final String API_KEY = "AIzaSyCltQOpFTDpMgmUhI_gPJVA9nSwEycFIuM";
    private static final String APPLICATION_NAME = "ChatBot";
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance();
    private static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance();

    private static ArrayList<String> queryGoogleBooks(JsonFactory jsonFactory, String query) throws Exception {

        ArrayList<String> bookList = new ArrayList<>();
        String bookInfo = new String();

        // Set up Books client.
        final Books books = new Books.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                jsonFactory, null).setApplicationName(APPLICATION_NAME)
                .setGoogleClientRequestInitializer(new BooksRequestInitializer(API_KEY))
                .build();
        // Set query string and filter only Google eBooks.
        System.out.println("Query: [" + query + "]");
        Books.Volumes.List volumesList = books.volumes().list(query);
        volumesList.setLangRestrict("ru");

        // Execute the query.
        Volumes volumes = volumesList.execute();
        if (volumes.getTotalItems() == 0 || volumes.getItems() == null) {
            System.out.println("No matches found.");
            return null;
        }
        int num = 0;
        for (Volume volume : volumes.getItems()) {
            num++;
            Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();
            Volume.SaleInfo saleInfo = volume.getSaleInfo();

            // Имя автора
            String authorsNames = "";
            java.util.List<String> authors = volumeInfo.getAuthors();
            if (authors != null && !authors.isEmpty()) {
                for (int i = 0; i < authors.size(); ++i) {
                    authorsNames += authors.get(i);
                    if (i < authors.size() - 1) {
                        authorsNames += ", ";
                    }
                }
            }

            // Description (if any).
            String description = "";
            if (volumeInfo.getDescription() != null && volumeInfo.getDescription().length() > 0) {
                description = volumeInfo.getDescription();
            }

            // Access status.
            String message = "Дополнительная информация об этой книге доступна в электронных книгах Google по адресу:";
            String link = volumeInfo.getInfoLink();

            if (volumeInfo.getTitle().length() > 0 && authorsNames.length() > 0 && description.length() > 0) {
                bookInfo = volumeInfo.getTitle()
                        + "\n\nАвтор: " + authorsNames
                        + "\n\n\n" + description
                        + "\n\n\n" + message
                        + "\n\n" + link;
                bookList.add(bookInfo);
            }
        }
        return bookList;
    }

    public ArrayList<String> searchBook(String typeQuery, String queryStr) {
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        try {

            String prefix = null;
            if (typeQuery.equals("по названию")) {
                prefix = "intitle:";
            } else if (typeQuery.equals("по автору")) {
                prefix = "inauthor:";
            } else if (typeQuery.equals("по категориям")) {
                prefix = "subject:";
            }

            String query = prefix + "<" + queryStr + ">";
            try {
                return queryGoogleBooks(jsonFactory, query);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(0);
        return null;
    }
}