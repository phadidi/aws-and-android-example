import java.util.Set;
import java.util.HashSet;

public class Movie {
    private String title;
    private int year;
    private String director;
    private Set<String> stars;
    private Set<String> genres;
    private float rating;

    public Movie() {
        title = "";
        year = 0;
        director = "";
        stars = new HashSet<String>();
        genres = new HashSet<String>();
        rating = 0;
    }

    public Movie(String rowTitle, int rowYear, String rowDirector, float rowRating) {
        title = rowTitle;
        year = rowYear;
        director = rowDirector;
        stars = new HashSet<String>();
        genres = new HashSet<String>();
        rating = rowRating;

    }

    public int addStar(String rowStar) {
        if (stars.size() < 3 && !stars.contains(rowStar)) {
            stars.add(rowStar);
            return 1;
        }
        // return 0 if nothing is added
        return 0;
    }

    public int addGenre(String rowGenre) {
        if (genres.size() < 3 && !genres.contains(rowGenre)) {
            genres.add(rowGenre);
            return 1;
        }
        // return 0 if nothing is added
        return 0;
    }

    public String getTitle(){
        return this.title;
    }

    public int getYear(){
        return this.year;
    }

    public String getDirector(){
        return this.director;
    }

    public Set<String> getStars(){
        return this.stars;
    }

    public Set<String> getGenres(){
        return this.genres;
    }

    public float getRating() { return this.rating; }

    public void setTitle(String title) { this.title = title; }

    public void setYear(int year) { this.year = year; }

    public void setDirector(String director) { this.director = director; }

    public void setRating(float rating) { this.rating = rating; }
}
