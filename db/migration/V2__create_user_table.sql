create table Users(
  id UUID default UUID(),
  username VARCHAR(20),
  password VARCHAR(50),
  PRIMARY KEY(id)
)
