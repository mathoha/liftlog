CREATE TABLE Workout(
  workoutID INT NOT NULL AUTO_INCREMENT,
  workout_date DATE,
  workout_time TIME,
  duration INT,
  shape INT,
  performance INT,
  note VARCHAR(1000),
  PRIMARY KEY(workoutID));

CREATE TABLE Exercise(
  exerciseID INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(80),
  type VARCHAR(40),
  PRIMARY KEY(exerciseID));

CREATE TABLE Equipment(
  equipmentID INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(80),
  description VARCHAR(1000),
  PRIMARY KEY(equipmentID));

CREATE TABLE Regular_Exercise(
  exerciseID INT,
  description VARCHAR(1000),
  PRIMARY KEY(exerciseID),
  FOREIGN KEY(exerciseID) REFERENCES Exercise(exerciseID)
  ON DELETE CASCADE ON UPDATE CASCADE);

CREATE TABLE Equipment_Exercise(
  exerciseID INT,
  equipmentID INT,
  PRIMARY KEY(exerciseID, equipmentID),
  FOREIGN KEY(equipmentID) REFERENCES Equipment(equipmentID)
  ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(exerciseID) REFERENCES Exercise(exerciseID)
  ON DELETE CASCADE ON UPDATE CASCADE);

CREATE TABLE Exercise_In_Workout(
  workoutID INT,
  exerciseID INT,
  set_nr INT,
  PRIMARY KEY(workoutID,exerciseID,set_nr),
  FOREIGN KEY(workoutID) REFERENCES Workout(workoutID)
  ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(exerciseID) REFERENCES Exercise(exerciseID)
  ON DELETE CASCADE ON UPDATE CASCADE);

CREATE TABLE Regular_Exercise_In_Workout(
  workoutID INT,
  exerciseID INT,
  set_nr INT,
  set_comment VARCHAR(1000),
  PRIMARY KEY(workoutID,exerciseID,set_nr),
  FOREIGN KEY(workoutID,exerciseID,set_nr)
  REFERENCES Exercise_In_Workout(workoutID,exerciseID,set_nr)
  ON DELETE CASCADE ON UPDATE CASCADE);

CREATE TABLE Equipment_Exercise_In_Workout(
  workoutID INT,
  exerciseID INT,
  set_nr INT,
  kilos INT,
  reps INT,
  PRIMARY KEY(workoutID,exerciseID,set_nr),
  FOREIGN KEY(workoutID,exerciseID,set_nr)
  REFERENCES Exercise_In_Workout(workoutID,exerciseID,set_nr)
  ON DELETE CASCADE ON UPDATE CASCADE);

CREATE TABLE Exercise_Group(
  groupID INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(80),
  PRIMARY KEY(groupID));

CREATE TABLE Exercise_In_Group(
  exerciseID INT,
  groupID INT,
  PRIMARY KEY(exerciseID,groupID),
  FOREIGN KEY(groupID) REFERENCES Exercise_Group(groupID)
  ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(exerciseID) REFERENCES Exercise(exerciseID)
  ON DELETE CASCADE ON UPDATE CASCADE);


INSERT INTO Workout (workout_date, workout_time, duration, shape, performance, note)
  VALUES
  ("2018-01-01","22:30:00", 90, 8, 9, "bra økt. deadlift føltes bra!"),
  ("2018-02-02","21:30:00", 60, 4, 5, "ikke like god følelse i dag"),
  ("2018-03-03","15:00:00", 85, 9, 10, "grei økt"),
  ("2018-03-10","15:00:00", 85, 9, 10, "supersterk i dag. Ny PR på deadlift");

INSERT INTO Exercise (name)
  VALUES
  ("deadlift"),("bench press"),
  ("box jumps"),("leg press");

INSERT INTO Equipment (name, description)
  VALUES
  ("barbell","20 kg normal barbell"),
  ("bench","flat bench with 20 kg barbell"),
  ("leg press machine","decline leg press machine with adjustable angles");

INSERT INTO Regular_Exercise (exerciseID, description)
  VALUES
  (3,"explosive jump onto box");

INSERT INTO Equipment_Exercise (exerciseID,equipmentID)
  VALUES
  (1,1),(2,2),(4,1);

INSERT INTO Exercise_In_Workout (workoutID, exerciseID, set_nr)
  VALUES
  (1,1,1),(1,1,2), (1,1,3),
  (2,1,1),(2,1,2), (2,1,3),
  (3,1,1),(3,1,2), (3,1,3),(3,1,4),
  (4,1,1),(4,1,2), (4,1,3),(4,1,4),

  (1,4,1),(1,4,2), (1,4,3),
  (2,4,1),(2,4,2), (2,4,3),
  (3,4,1),(3,4,2), (3,4,3),(3,4,4),
  (4,4,1),(4,4,2), (4,4,3),(4,4,4),

  (1,2,1),(1,2,2), (1,2,3),
  (2,2,1),(2,2,2), (2,2,3),
  (3,2,1),(3,2,2), (3,2,3),(3,2,4),
  (4,2,1),(4,2,2), (4,2,3),(4,2,4),

  (1,3,1),(1,3,2),
  (3,3,1),(3,3,2)
  ;

INSERT INTO Regular_Exercise_In_Workout (workoutID, exerciseID, set_nr, set_comment)
  VALUES
  (1,3,1,"1m box"),(1,3,2,"1.2m box"),
  (3,3,1,"1m box"),(3,3,2,"1.3m box")
  ;

INSERT INTO Equipment_Exercise_In_Workout (workoutID, exerciseID, set_nr, kilos, reps)
  VALUES
  (1,1,1,100,5),(1,1,2,120,3), (1,1,3,110,7),
  (2,1,1,90,7),(2,1,2,110,6), (2,1,3,110,4),
  (3,1,1,100,5),(3,1,2,125,3), (3,1,3,130,2),(3,1,4,130,1),
  (4,1,1,100,8),(4,1,2,125,2), (4,1,3,135,3),(4,1,4,140,1),

  (1,4,1,100,5),(1,4,2,120,7), (1,4,3,110,6),
  (2,4,1,90,3),(2,4,2,110,4), (2,4,3,100,6),
  (3,4,1,120,8),(3,4,2,120,9), (3,4,3,125,10),(3,4,4,130,5),
  (4,4,1,120,10),(4,4,2,125,7), (4,4,3,140,5),(4,4,4,150,2),

  (1,2,1,60,5),(1,2,2,55,7), (1,2,3,50,6),
  (2,2,1,65,3),(2,2,2,65,4), (2,2,3,60,6),
  (3,2,1,50,10),(3,2,2,60,8), (3,2,3,70,4),(3,2,4,75,1),
  (4,2,1,50,12),(4,2,2,65,5), (4,2,3,70,5),(4,2,4,80,3)
  ;

INSERT INTO Exercise_Group (name)
  VALUES
  ("back"),("legs"),("chest")
  ;

INSERT INTO Exercise_In_Group (exerciseID,groupID)
  VALUES
  (1,1),(1,2),(2,3),(3,2),(4,1)
  ;



