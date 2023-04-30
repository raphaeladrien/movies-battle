create table if not exists Rounds(
  id UUID default UUID(),
  game_id UUID not null,
  first_movie UUID not null,
  second_movie UUID not null,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  PRIMARY KEY(id),
  foreign key (first_movie) references Movies(id),
  foreign key (second_movie) references Movies(id)
)
