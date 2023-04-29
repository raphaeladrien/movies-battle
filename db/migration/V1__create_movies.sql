create table Movies(
  id UUID default UUID(),
  title VARCHAR(255),
  release_year INT,
  director VARCHAR(255),
  actors VARCHAR(500),
  imdbRating FLOAT,
  imdbVotes LONG,
  PRIMARY KEY(id)
)
