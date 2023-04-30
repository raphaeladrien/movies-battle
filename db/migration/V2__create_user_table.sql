create table if not exists Users(
  id UUID default UUID(),
  username VARCHAR(20),
  password VARCHAR(50),
  role VARCHAR(255),
  PRIMARY KEY(id)
)
