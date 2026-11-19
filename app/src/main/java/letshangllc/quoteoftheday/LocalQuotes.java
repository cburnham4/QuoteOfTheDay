package letshangllc.quoteoftheday;

import java.util.Random;

/**
 * Bundled motivational quotes so the app works fully offline.
 * Replaces the former remote quotes.rest API and Firebase Realtime Database feed.
 */
public final class LocalQuotes {

    private static final String[] QUOTES = {
            "In order to succeed, we must first believe we can.\n- Nikos Kazantzakis",
            "The only way to do great work is to love what you do.\n- Steve Jobs",
            "It does not matter how slowly you go as long as you do not stop.\n- Confucius",
            "Believe you can and you're halfway there.\n- Theodore Roosevelt",
            "The future belongs to those who believe in the beauty of their dreams.\n- Eleanor Roosevelt",
            "Success is not final, failure is not fatal: it is the courage to continue that counts.\n- Winston Churchill",
            "Don't watch the clock; do what it does. Keep going.\n- Sam Levenson",
            "The only limit to our realization of tomorrow is our doubts of today.\n- Franklin D. Roosevelt",
            "What you get by achieving your goals is not as important as what you become by achieving your goals.\n- Zig Ziglar",
            "Act as if what you do makes a difference. It does.\n- William James",
            "You are never too old to set another goal or to dream a new dream.\n- C.S. Lewis",
            "Hardships often prepare ordinary people for an extraordinary destiny.\n- C.S. Lewis",
            "Start where you are. Use what you have. Do what you can.\n- Arthur Ashe",
            "Everything you've ever wanted is on the other side of fear.\n- George Addair",
            "Quality is not an act, it is a habit.\n- Aristotle",
            "The best time to plant a tree was 20 years ago. The second best time is now.\n- Chinese Proverb",
            "Do what you can, with what you have, where you are.\n- Theodore Roosevelt",
            "It always seems impossible until it's done.\n- Nelson Mandela",
            "If you want to lift yourself up, lift up someone else.\n- Booker T. Washington",
            "Keep your face always toward the sunshine—and shadows will fall behind you.\n- Walt Whitman",
            "The secret of getting ahead is getting started.\n- Mark Twain",
            "You miss 100% of the shots you don't take.\n- Wayne Gretzky",
            "Whether you think you can or you think you can't, you're right.\n- Henry Ford",
            "I have not failed. I've just found 10,000 ways that won't work.\n- Thomas Edison",
            "Strive not to be a success, but rather to be of value.\n- Albert Einstein",
            "The harder I work, the luckier I get.\n- Samuel Goldwyn",
            "Dream big and dare to fail.\n- Norman Vaughan",
            "A journey of a thousand miles begins with a single step.\n- Lao Tzu",
            "Don't be afraid to give up the good to go for the great.\n- John D. Rockefeller",
            "Opportunities don't happen. You create them.\n- Chris Grosser",
            "If you're going through hell, keep going.\n- Winston Churchill",
            "Fall seven times, stand up eight.\n- Japanese Proverb",
            "Happiness is not something ready made. It comes from your own actions.\n- Dalai Lama",
            "The only person you are destined to become is the person you decide to be.\n- Ralph Waldo Emerson",
            "Go confidently in the direction of your dreams.\n- Henry David Thoreau",
            "What lies behind us and what lies before us are tiny matters compared to what lies within us.\n- Ralph Waldo Emerson",
            "You have within you right now, everything you need to deal with whatever the world can throw at you.\n- Brian Tracy",
            "Perseverance is not a long race; it is many short races one after the other.\n- Walter Elliot",
            "Courage is resistance to fear, mastery of fear—not absence of fear.\n- Mark Twain",
            "The best revenge is massive success.\n- Frank Sinatra",
            "Your time is limited, so don't waste it living someone else's life.\n- Steve Jobs",
            "Be yourself; everyone else is already taken.\n- Oscar Wilde",
            "We are what we repeatedly do. Excellence, then, is not an act, but a habit.\n- Aristotle",
            "Limitations live only in our minds. But if we use our imaginations, our possibilities become limitless.\n- Jamie Paolinetti",
            "Do not wait to strike till the iron is hot; but make it hot by striking.\n- William Butler Yeats",
            "Great things are done by a series of small things brought together.\n- Vincent Van Gogh",
            "Motivation is what gets you started. Habit is what keeps you going.\n- Jim Ryun",
            "Push yourself, because no one else is going to do it for you.\n- Unknown",
            "Little by little, one travels far.\n- J.R.R. Tolkien",
            "The way to get started is to quit talking and begin doing.\n- Walt Disney"
    };

    private LocalQuotes() {
    }

    public static String randomQuote() {
        return QUOTES[new Random().nextInt(QUOTES.length)];
    }

    public static int size() {
        return QUOTES.length;
    }
}
