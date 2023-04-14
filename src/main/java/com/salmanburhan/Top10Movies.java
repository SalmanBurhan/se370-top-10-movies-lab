package com.salmanburhan;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;

import java.awt.Font;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.*;

public final class Top10Movies extends JFrame {
    
    private JSONArray popularMovies;
    private JList<String> movieList;
    private JLabel moviePoster;
    private JLabel movieTitleLabel;
    private JLabel movieReleaseLabel;
    private JTextArea movieSynopsisLabel;

    public Top10Movies() {

        /* Initialize The Superclass, A.K.A. JFrame */
        super("Top 10 Movies");
        
        /* Set The Default "Mode" When User Clicks The "Close"
         * Button. When Not Assigned Manually, The Default
         * Action Is JFrame.HIDE_ON_CLOSE, Which Gives The
         * Illusion That The Execution Has Ended, When In
         * Reality It Is Still Running and Wasting Memory.
         * This Is Good Practice To Specify Because We Are
         * Not Running Any Background Tasks And We Want The
         * Kill The Executable When The User "Closes The App".
         */
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* Set The Layout Of The JFrame/Window.
         * We Specify The Layout Of This JPanel To Be Of The
         * Type "FlowLayout" Because It Makes Aligning UI
         * Components And Location Management Simplier.
         * Flow Layout Will Add Each Component Directly To
         * The Right Of The Previously Added Component, Saving
         * The Need To Calculate X-Axis values, This Effect
         * Trickles Down To Y-Axis Calculation As, The UI
         * Components Will "Flow" Onto The Next Line As Needed.
         */
        this.getContentPane().setLayout(new FlowLayout());

        /* Initialize & Layout All The UI Components */
        this.layoutUI();

        /* Fetch The Top 10 Popular Movies */
        this.getPopularMovies();
    }

    public void layoutUI() {

        /* Create A Panel Which Will Contain A List View
         * Component Whose Data Source Is The List Of Movies.
         * We Then Add This List View To The listPanel Container
         * We Created First.
         * 
         * We Don't Want To Let The User Hold The Shift Key & Select
         * Multiple Items... So We Need To Set The Selection Mode.
         */
        JPanel listPanel = new JPanel();
        this.movieList = new JList<String>();
        this.movieList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.movieList.setPreferredSize(new Dimension(300, 500)); // Notice Width Here
        this.movieList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                updateMovieDetails(movieList.getSelectedIndex());
            }
        });
        listPanel.add(movieList);

        /* Create A Panel Which Will Display A Modeled View
         * Whose Subcomponents/Subviews Will Reflect The
         * Values Of A Given Model A.K.A. The Movie.
         */
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.PAGE_AXIS));

        this.moviePoster = new JLabel(new ImageIcon());
        /* The Standard Movie Poster Ratio Is 2:3 Width To Height aka 0.667 */
        this.moviePoster.setPreferredSize(new Dimension(500, 334));
        this.moviePoster.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailPanel.add(this.moviePoster);
        
        this.movieTitleLabel = new JLabel();
        this.movieTitleLabel.setPreferredSize(new Dimension(100, 25));
        this.movieTitleLabel.setFont(this.movieList.getFont().deriveFont(Font.BOLD));
        this.movieTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailPanel.add(this.movieTitleLabel);

        this.movieReleaseLabel = new JLabel();
        this.movieReleaseLabel.setPreferredSize(new Dimension(100, 25));
        this.movieReleaseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailPanel.add(this.movieReleaseLabel);

        this.movieSynopsisLabel = new JTextArea();
        this.movieSynopsisLabel.setPreferredSize(new Dimension(500, 200)); // Notice Width Here
        this.movieSynopsisLabel.setLineWrap(true);
        this.movieSynopsisLabel.setWrapStyleWord(true);
        this.movieSynopsisLabel.setEditable(false);
        this.movieSynopsisLabel.setOpaque(false);
        this.movieSynopsisLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailPanel.add(this.movieSynopsisLabel);

        /* Create A SplitPanel Which Will Encapsulate The Two
         * Panels We Created Earlier, Creating An Associated
         * Relationship Between The Two & Allowing Us To Treat
         * Both Of Them As A Single Object When Adding Them To
         * The JFrame / Window.
         */
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, detailPanel);
        this.add(splitPane);
        
        /* An Alternative To this.setSize(int width, int height);
         * Allows JFrame To "self size" in order to fit all the
         * subcomponents added to it.
         */
        this.pack();

        /* Present The Frame aka Window */
        this.setVisible(true);

        /*
         * A NOTE ON THE WIDTH CHOICES, SPECIFICALLY 500 and 100...
         * Since We Are Using The "FlowLayout" At The Top Level,
         * A.K.A. The JFrame Being Presented... These Two Widths
         * Are Of Significance, As The Layout Used The RATIO Of
         * These Two Largest And Smallest Preferred Widths To
         * Create This 20-80 Width Ratio Of The ListPanel To
         * The Detail Panel.
         */
    }

    public void getPopularMovies() {
        /*
        * Fetch The Popular Movies.
        * Extract The Release Year From The "release_date" JSON field.
        * This Field Is In The "yyyy-mm-dd". We Just Want "yyyy".
        * Handle Any Exceptions Thrown Here. The Only Line Of Code
        * That Can Throw Here Is The Parsing From A String To a Date
        * Object. However We Keep All The Code Related To It In This
        * "try" Block Because If It Fails To Parse The String For Any
        * Reason, Say, The Data Was Corrupt, Then We Can Just "continue".
        * In Other Words, We SKip This Movie Entirely And Move Onto The
        * Next. Because The Related Subsequent Code Is Then Skipped, We
        * Won't Run Into A Snowballing Of Errors When, For Example, We
        * Try To Assign A Formatted String With A Now NULL Object, etc.
        */
        this.popularMovies = TMDB.popular();
        String[] movieListStrings = new String[10];
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
        for (int i=0; i < 10; i++) {
            try {
                JSONObject movie = this.popularMovies.getJSONObject(i);
                String title = movie.getString("title");
                Date release_date = dateFormatter.parse(movie.getString("release_date"));
                String release_year = yearFormatter.format(release_date);
                movieListStrings[i] = String.format("%s (%s)", title, release_year);    
            } catch (ParseException e) {
                continue;
            }

        }
        
        this.movieList.setListData(movieListStrings);
    }

    /*
     * Update The Movie Details Pane UI Components To Reflect
     * The Movie @ Index `selectedIndex` in this.popularMovies.
     * NOTICE HERE that we DID NOT completely group the code
     * unrelated to the release year parsing in the 'try' Block.
     * This Is Because, If We Are Updating The UI, And Assuming
     * We Can Update Everything Else BUT The Year, We Will Do
     * Just That... Being Sure To Specify That The Release Year
     * Is "Unknown".
     */
    private void updateMovieDetails(int selectedIndex) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
        JSONObject movie = this.popularMovies.getJSONObject(selectedIndex);

        this.movieTitleLabel.setText(
            movie.getString("title")
        );
        this.movieSynopsisLabel.setText(
            movie.getString("overview")
        );

        try {
            String poster_path = String.format("https://image.tmdb.org/t/p/w500%s", movie.getString("poster_path"));
            URL poster_url = new URL(poster_path);
            Image poster_image = ImageIO.read(poster_url);
            Image scaled_poster_image = poster_image.getScaledInstance(
                -1,
                this.moviePoster.getHeight(),
                Image.SCALE_SMOOTH
            );
            this.moviePoster.setIcon(new ImageIcon(scaled_poster_image));
        } catch (MalformedURLException ex) {
            System.out.println("Malformed URL Error");
        } catch (IOException iox) {
            System.out.println("Image Load Error");
        }

        try {
            Date release_date = dateFormatter.parse(movie.getString("release_date"));
            String release_year = yearFormatter.format(release_date);
            this.movieReleaseLabel.setText(release_year);    
        } catch (ParseException e) {
            this.movieReleaseLabel.setText("Year Unknown");
        }
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        new Top10Movies();
    }
}
