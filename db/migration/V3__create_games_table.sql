create table if not exists Games(
  id UUID default UUID(),
  user_id UUID not null,
  errors NUMERIC not null default 0,
  in_progress BOOLEAN not null,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  PRIMARY KEY(id),
  foreign key (id_user) references Users(id)
)
