create table if not exists Movies(
  id UUID default UUID(),
  title VARCHAR(255),
  release_year INT,
  director VARCHAR(255),
  actors VARCHAR(500),
  imdbRating FLOAT,
  imdbVotes LONG,
  imdbId VARCHAR(15),
  PRIMARY KEY(id)
)
