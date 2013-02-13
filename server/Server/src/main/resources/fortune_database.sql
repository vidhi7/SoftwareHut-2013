CREATE DATABASE IF NOT EXISTS fortune;
USE fortune; 
DROP TABLE IF EXISTS Quotes;
CREATE TABLE IF NOT EXISTS Quotes
(
Quote_ID INTEGER,
Quote VARCHAR(1000)
);

INSERT INTO Quotes VALUE ("1" , "A black cat crossing your path signifies that the animal is going somewhere.-- Groucho Marx");
INSERT INTO Quotes VALUE ("2" , "A friend of mine is into Voodoo Acupuncture. You don''t have to go. You''ll just be walking down the street and...  Ooohh, that''s much better.-- Steven Wright");
INSERT INTO Quotes VALUE ("3" , "A lot of people are afraid of heights.  Not me.  I''m afraid of widths.-- Steven Wright");
INSERT INTO Quotes VALUE ("4" , "A possum must be himself, and being himself he is honest.-- Walt Kelly");
INSERT INTO Quotes VALUE ("5" , "All men are mortal.  Socrates was mortal.  Therefore, all men are Socrates.-- Woody Allen");
INSERT INTO Quotes VALUE ("6" , "Bernard Shaw is an excellent man; he has not an enemy in the world, and none of his friends like him either.-- Oscar Wilde");
INSERT INTO Quotes VALUE ("7" , "Comedy, like Medicine, was never meant to be practiced by the general public.");
INSERT INTO Quotes VALUE ("8" , "Decorate your home.  It gives the illusion that your life is more interesting than it really is.-- C. Schulz");
INSERT INTO Quotes VALUE ("9" , "Eternity is a terrible thought.  I mean, where''s it going to end?-- Tom Stoppard");
INSERT INTO Quotes VALUE ("10" , "For my birthday I got a humidifier and a de-humidifier...  I put them in the same room and let them fight it out.-- Steven Wright");