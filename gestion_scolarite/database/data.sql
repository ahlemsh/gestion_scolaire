USE gestion_scolarite_demo;

-- 1) Groupes (16)
INSERT INTO groupe (id, nom, effectif_max, niveau) VALUES
(1,'Groupe A',15,'L1'),(2,'Groupe B',15,'L1'),
(3,'Groupe A',15,'L2'),(4,'Groupe B',15,'L2'),
(5,'Groupe A',15,'L3'),(6,'Groupe B',15,'L3'),
(7,'Groupe A',15,'M1'),(8,'Groupe B',15,'M1'),
(9,'Groupe A',15,'M2'),(10,'Groupe B',15,'M2'),
(11,'Groupe A',15,'ING1'),(12,'Groupe B',15,'ING1'),
(13,'Groupe A',15,'ING2'),(14,'Groupe B',15,'ING2'),
(15,'Groupe A',15,'ING3'),(16,'Groupe B',15,'ING3');

-- 2) Matières (19)
INSERT INTO matiere (id, nom, coefficient, volume_horaire) VALUES
(1,'Base de données',3,45),
(2,'Big Data',3,45),
(3,'Réseau',2,30),
(4,'Sécurité des Réseaux',2,30),
(5,'Linux',2,30),
(6,'Développement web',2,30),
(7,'Open Source',1,20),
(8,'Méthodologie de gestion de projet',1,20),
(9,'Programmation système et réseau',2,30),
(10,'Programmation Java',3,45),
(11,'Français',1,20),
(12,'Anglais',1,20),
(13,'IoT',2,30),
(14,'Python',2,30),
(15,'Cloud Computing',2,30),
(16,'Intelligence Artificielle',3,45),
(17,'Machine Learning',3,45),
(18,'Algorithmique',2,30),
(19,'Mathématiques',2,30);

-- 3) Enseignants (ids fixes, téléphones commencent par 20/50/90)
INSERT INTO enseignant (id, nom, prenom, specialite, email, telephone) VALUES
(1,'Ben Salah','Nour','Data','nour.bensalah@exemple.tn','20512345'),
(2,'Trabelsi','Sana','Réseaux','sana.trabelsi@exemple.tn','50987654'),
(3,'Mhiri','Yassine','Linux','yassine.mhiri@exemple.tn','90334455'),
(4,'Khemiri','Oumaima','Web','oumaima.khemiri@exemple.tn','20598765'),
(5,'Jlassi','Amine','Open Source','amine.jlassi@exemple.tn','50911223'),
(6,'Gharbi','Hela','Gestion de projet','hela.gharbi@exemple.tn','90566778'),
(7,'Sassi','Mohamed','Programmation Systèmes','mohamed.sassi@exemple.tn','20123456'),
(8,'Bouazizi','Sara','Java','sara.bouazizi@exemple.tn','50987612'),
(9,'Kallel','Ines','Français','ines.kallel@exemple.tn','90112233'),
(10,'Ayari','Walid','Anglais','walid.ayari@exemple.tn','20556677'),
(11,'Ben Youssef','Malek','IoT','malek.benyoussef@exemple.tn','50990011'),
(12,'Masmoudi','Rim','Python','rim.masmoudi@exemple.tn','90445566'),
(13,'Feki','Sami','Cloud','sami.feki@exemple.tn','20199887'),
(14,'Zoghlami','Ameni','IA/ML','ameni.zoghlami@exemple.tn','50945566'),
(15,'Hachicha','Karim','Algorithmique','karim.hachicha@exemple.tn','90330011'),
(16,'Chaabane','Nadia','Mathématiques','nadia.chaabane@exemple.tn','20577889');

-- 4) Étudiants : 5 par groupe (80) (on reprend tes noms existants)
-- L1A (1)
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Ben Ali','Houssem','2005-01-12','houssem.benali@exemple.tn','20500111',1),
('Trabelsi','Maram','2005-02-03','maram.trabelsi@exemple.tn','50500222',1),
('Gharbi','Aziz','2005-03-19','aziz.gharbi@exemple.tn','90500333',1),
('Jlassi','Rahma','2005-04-22','rahma.jlassi@exemple.tn','20500444',1),
('Khelifi','Ala','2005-05-05','ala.khelifi@exemple.tn','50500555',1);

-- L1B (2)
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Sassi','Yasmine','2005-06-14','yasmine.sassi@exemple.tn','90500666',2),
('Kallel','Fares','2005-07-09','fares.kallel@exemple.tn','20500777',2),
('Ayari','Meriem','2005-08-30','meriem.ayari@exemple.tn','50500888',2),
('Bouazizi','Skander','2005-09-11','skander.bouazizi@exemple.tn','90500999',2),
('Chaabane','Rania','2005-10-26','rania.chaabane@exemple.tn','20501010',2);

-- L2A (3)
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Ben Romdhane','Hiba','2004-01-15','hiba.benromdhane@exemple.tn','50501111',3),
('Masmoudi','Taha','2004-02-07','taha.masmoudi@exemple.tn','90501212',3),
('Feki','Sarra','2004-03-21','sarra.feki@exemple.tn','20501313',3),
('Zoghlami','Ilyes','2004-04-10','ilyes.zoghlami@exemple.tn','50501414',3),
('Mhiri','Nour','2004-05-18','nour.mhiri@exemple.tn','90501515',3);

-- L2B (4)
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Khemiri','Omar','2004-06-02','omar.khemiri@exemple.tn','20501616',4),
('Hachicha','Eya','2004-07-27','eya.hachicha@exemple.tn','50501717',4),
('Triki','Rayen','2004-08-08','rayen.triki@exemple.tn','90501818',4),
('Sahli','Tasnim','2004-09-14','tasnim.sahli@exemple.tn','20501919',4),
('Baccouche','Wassim','2004-10-29','wassim.baccouche@exemple.tn','50502020',4);

-- L3A (5)
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Ben Amor','Salma','2003-01-09','salma.benamor@exemple.tn','90502121',5),
('Bouslama','Ahmed','2003-02-12','ahmed.bouslama@exemple.tn','20502222',5),
('Zitouni','Ines','2003-03-25','ines.zitouni@exemple.tn','50502323',5),
('Cherif','Sami','2003-04-06','sami.cherif@exemple.tn','90502424',5),
('Haddad','Ameni','2003-05-17','ameni.haddad@exemple.tn','20502525',5);

-- L3B (6)
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Mansour','Youssef','2003-06-30','youssef.mansour@exemple.tn','50502626',6),
('Khiari','Rim','2003-07-13','rim.khiari@exemple.tn','90502727',6),
('Kammoun','Bilel','2003-08-04','bilel.kammoun@exemple.tn','20502828',6),
('Nabli','Hela','2003-09-19','hela.nabli@exemple.tn','50502929',6),
('Saidi','Mohamed','2003-10-08','mohamed.saidi@exemple.tn','90503030',6);

-- M1A (7)
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Karray','Faten','2002-01-11','faten.karray@exemple.tn','20503131',7),
('Heni','Adem','2002-02-20','adem.heni@exemple.tn','50503232',7),
('Mrad','Yara','2002-03-03','yara.mrad@exemple.tn','90503333',7),
('Ben Fredj','Seif','2002-04-16','seif.benfredj@exemple.tn','20503434',7),
('Tounsi','Marwa','2002-05-27','marwa.tounsi@exemple.tn','50503535',7);

-- M1B (😎
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Sfar','Anis','2002-06-09','anis.sfar@exemple.tn','90503636',8),
('Oueslati','Sinda','2002-07-22','sinda.oueslati@exemple.tn','20503737',8),
('Guezguez','Malek','2002-08-05','malek.guezguez@exemple.tn','50503838',8),
('Klibi','Hanen','2002-09-18','hanen.klibi@exemple.tn','90503939',8),
('Khlif','Rayan','2002-10-30','rayan.khlif@exemple.tn','20504040',8);

-- M2A (9)
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Ben Saad','Lina','2001-01-08','lina.bensaad@exemple.tn','50504141',9),
('Bouzid','Khalil','2001-02-14','khalil.bouzid@exemple.tn','90504242',9),
('Smaoui','Nada','2001-03-26','nada.smaoui@exemple.tn','20504343',9),
('Ben Mahmoud','Fares','2001-04-02','fares.benmahmoud@exemple.tn','50504444',9),
('Hentati','Maha','2001-05-19','maha.hentati@exemple.tn','90504545',9);

-- M2B (10)
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Ghedira','Sofien','2001-06-27','sofien.ghedira@exemple.tn','20504646',10),
('Kassab','Eline','2001-07-15','eline.kassab@exemple.tn','50504747',10),
('Kefi','Amir','2001-08-01','amir.kefi@exemple.tn','90504848',10),
('Brahim','Rania','2001-09-23','rania.brahim@exemple.tn','20504949',10),
('Hachani','Tarek','2001-10-06','tarek.hachani@exemple.tn','50505050',10);

-- ING1A (11)
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Guizani','Meriem','2000-01-10','meriem.guizani@exemple.tn','90505151',11),
('Guesmi','Hedi','2000-02-22','hedi.guesmi@exemple.tn','20505252',11),
('Ben Salem','Nadia','2000-03-12','nadia.bensalem@exemple.tn','50505353',11),
('Hachicha','Skander','2000-04-25','skander.hachicha@exemple.tn','90505454',11),
('Hammami','Yassine','2000-05-07','yassine.hammami@exemple.tn','20505555',11);

-- ING1B (12)
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Bouderbala','Mouna','2000-06-19','mouna.bouderbala@exemple.tn','50505656',12),
('Kouki','Aymen','2000-07-03','aymen.kouki@exemple.tn','90505757',12),
('Mouelhi','Rim','2000-08-16','rim.mouelhi@exemple.tn','20505858',12),
('Mokhtar','Sami','2000-09-28','sami.mokhtar@exemple.tn','50505959',12),
('Zribi','Hiba','2000-10-09','hiba.zribi@exemple.tn','90506060',12);

-- ING2A (13)
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Belaid','Wassim','1999-01-05','wassim.belaid@exemple.tn','20506161',13),
('Gharbi','Eya','1999-02-18','eya.gharbi@exemple.tn','50506262',13),
('Ben Hmida','Maram','1999-03-11','maram.benhmida@exemple.tn','90506363',13),
('Jouini','Rayen','1999-04-24','rayen.jouini@exemple.tn','20506464',13),
('Kotti','Salma','1999-05-08','salma.kotti@exemple.tn','50506565',13);

-- ING2B (14)
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Ltaief','Amine','1999-06-20','amine.ltaief@exemple.tn','90506666',14),
('Maatoug','Ines','1999-07-14','ines.maatoug@exemple.tn','20506767',14),
('Dridi','Omar','1999-08-26','omar.dridi@exemple.tn','50506868',14),
('Bensalem','Rania','1999-09-03','rania.bensalem@exemple.tn','90506969',14),
('Toumi','Houssem','1999-10-17','houssem.toumi@exemple.tn','20507070',14);

-- ING3A (15)
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Ben Aissa','Maha','1998-01-13','maha.benaissa@exemple.tn','50507171',15),
('Koubaa','Seif','1998-02-07','seif.koubaa@exemple.tn','90507272',15),
('Hajji','Lina','1998-03-21','lina.hajji@exemple.tn','20507373',15),
('Khemakhem','Taha','1998-04-04','taha.khemakhem@exemple.tn','50507474',15),
('Ben Yedder','Sinda','1998-05-19','sinda.benyedder@exemple.tn','90507575',15);

-- ING3B (16)
INSERT INTO etudiant (nom, prenom, date_naissance, email, telephone, id_groupe) VALUES
('Khalfa','Anis','1998-06-11','anis.khalfa@exemple.tn','20507676',16),
('Ben Nasr','Yara','1998-07-29','yara.bennasr@exemple.tn','50507777',16),
('Labidi','Karim','1998-08-15','karim.labidi@exemple.tn','90507878',16),
('Bouhlel','Hanen','1998-09-06','hanen.bouhlel@exemple.tn','20507979',16),
('Khelil','Amir','1998-10-23','amir.khelil@exemple.tn','50508080',16);

-- 5) Séances

-- ===================== L1A (1) =====================
(1, 1, 1,  'LUN', '08:00', '10:00', 'Salle 12'),  -- BD
(1, 3, 2,  'MAR', '10:00', '12:00', 'Salle 05'),  -- Réseau
(1, 5, 3,  'MER', '08:00', '10:00', 'Salle 02'),  -- Linux

-- ===================== L1B (2) =====================
(2, 1, 1,  'LUN', '14:00', '16:00', 'Salle 13'),  -- BD (horaire différent)
(2, 3, 2,  'MAR', '16:00', '18:00', 'Salle 06'),  -- Réseau
(2, 5, 3,  'MER', '14:00', '16:00', 'Salle 03'),  -- Linux

-- ===================== L2A (3) =====================
(3, 10, 8, 'LUN', '08:00', '10:00', 'Salle 07'),  -- Java
(3, 4, 2,  'JEU', '10:00', '12:00', 'Salle 03'),  -- Sécurité Réseaux
(3, 6, 4,  'VEN', '08:00', '10:00', 'Salle 09'),  -- Web

-- ===================== L2B (4) =====================
(4, 10, 8, 'LUN', '14:00', '16:00', 'Salle 08'),
(4, 4, 2,  'JEU', '16:00', '18:00', 'Salle 04'),
(4, 6, 4,  'VEN', '14:00', '16:00', 'Salle 10'),

-- ===================== L3A (5) =====================
(5, 2, 1,  'MAR', '08:00', '10:00', 'Salle 01'),  -- Big Data (même prof que BD)
(5, 9, 7,  'MER', '10:00', '12:00', 'Salle 04'),  -- Prog sys & réseau
(5, 14, 12,'VEN', '08:00', '10:00', 'Salle 06'),  -- Python

-- ===================== L3B (6) =====================
(6, 2, 1,  'MAR', '14:00', '16:00', 'Salle 02'),
(6, 9, 7,  'MER', '16:00', '18:00', 'Salle 05'),
(6, 14, 12,'VEN', '14:00', '16:00', 'Salle 07'),

-- ===================== M1A (7) =====================
(7, 15, 13,'LUN', '08:00', '10:00', 'Salle 08'),  -- Cloud
(7, 16, 14,'JEU', '10:00', '12:00', 'Salle 10'),  -- IA
(7, 19, 16,'VEN', '10:00', '12:00', 'Salle 11'),  -- Maths

-- ===================== M1B (8) =====================
(8, 15, 13,'LUN', '14:00', '16:00', 'Salle 09'),
(8, 16, 14,'JEU', '16:00', '18:00', 'Salle 11'),
(8, 19, 16,'VEN', '16:00', '18:00', 'Salle 12'),

-- ===================== M2A (9) =====================
(9, 17, 14,'MAR', '08:00', '10:00', 'Salle 14'),  -- ML (même prof que IA)
(9, 13, 11,'MER', '10:00', '12:00', 'Salle 13'),  -- IoT
(9, 8,  6, 'JEU', '08:00', '10:00', 'Salle 15'),  -- Méthodo

-- ===================== M2B (10) =====================
(10, 17, 14,'MAR', '14:00', '16:00', 'Salle 15'),
(10, 13, 11,'MER', '16:00', '18:00', 'Salle 14'),
(10, 8,  6, 'JEU', '14:00', '16:00', 'Salle 16'),

-- ===================== ING1A (11) =====================
(11, 18, 15,'LUN', '10:00', '12:00', 'Salle 16'), -- Algo
(11, 7,  5, 'MAR', '08:00', '10:00', 'Salle 17'), -- Open Source
(11, 12, 10,'VEN', '14:00', '16:00', 'Salle 18'), -- Anglais

-- ===================== ING1B (12) =====================
(12, 18, 15,'LUN', '16:00', '18:00', 'Salle 17'),
(12, 7,  5, 'MAR', '14:00', '16:00', 'Salle 18'),
(12, 12, 10,'VEN', '16:00', '18:00', 'Salle 19'),

-- ===================== ING2A (13) =====================
(13, 11, 9, 'MER', '14:00', '16:00', 'Salle 19'), -- Français
(13, 6,  4, 'JEU', '10:00', '12:00', 'Salle 20'), -- Web
(13, 5,  3, 'SAM', '08:00', '10:00', 'Salle 21'), -- Linux

-- ===================== ING2B (14) =====================
(14, 11, 9, 'MER', '16:00', '18:00', 'Salle 20'),
(14, 6,  4, 'JEU', '16:00', '18:00', 'Salle 21'),
(14, 5,  3, 'SAM', '10:00', '12:00', 'Salle 22'),

-- ===================== ING3A (15) =====================
(15, 2,  1, 'LUN', '08:00', '10:00', 'Salle 22'), -- Big Data
(15, 3,  2, 'MAR', '10:00', '12:00', 'Salle 23'), -- Réseau
(15, 16, 14,'JEU', '08:00', '10:00', 'Salle 24'), -- IA

-- ===================== ING3B (16) =====================
(16, 2,  1, 'LUN', '14:00', '16:00', 'Salle 23'),
(16, 3,  2, 'MAR', '16:00', '18:00', 'Salle 24'),
(16, 16, 14,'JEU', '14:00', '16:00', 'Salle 25');
