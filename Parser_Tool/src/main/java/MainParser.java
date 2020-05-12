public class MainParser {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        // insert movies, genres, and update genres_in_movies
        MovieBatchInsert mbi = new MovieBatchInsert();
        mbi.insertMovieAndGenres();

        // insert stars and update stars_in_movies
        StarBatchInsert sbi = new StarBatchInsert();
        sbi.insertStars();
    }
}
