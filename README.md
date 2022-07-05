# clinic-assessment
 Clinic project assessment

*** Some additional tasks have been included in the BONUS folder ***

 Port = 8090
 Prefix to endpoint = /api
 _______________________________________________________________________________________________________________________________________
|  Endpoint                            |  Mapping  | Requirement                                                 | Access               |
| ------------------------------------ | --------- | ----------------------------------------------------------- | -------------------- |
| /register                            | PUT       | Allow all users to register                                 | All                  |
| /login                               | POST      | Allow users to login                                        | All                  |
| /doctors                             | GET       | View list of doctors                                        | All                  |
| /doctors/{id}                        | GET       | View Doctor information                                     | All                  |
| /doctors/{id}/slots?date=            | GET       | View Doctors available slots                                | Doctor, Patient, CA  |
| /appointment/book                    | PUT       | Book an appointment with a doctor                           | Patient              |
| /appointment/cancel                  | POST      | Cancel appointment                                          | Doctor, CA           |
| /doctors/all/slots?date=             | GET       | View availability of all Doctors                            | Doctor, Patient, CA  |
| /appointment/{id}/details            | GET       | View appointment details                                    | Doctor, Patient      |
| /appointment/all?patient_id=         | GET       | View patient appointment history                            | Doctor, Patient      |
| /doctors/busy/{date}                 | GET       | View doctors with the most appointments in a given day      | CA                   |
| /doctors/busy/{date}{minimum-hours}  | GET       | View doctors who have 6+ hours total appointments in a day  | CA                   |
 ---------------------------------------------------------------------------------------------------------------------------------------
 
 Deployment information (Azure):
 1) Navigate to App Services>Create Web App and set up the web app
 2) From the web app page, navigate to Deployment>Deployment Center
 3) Enter github link
 4) Configure and deploy


 Additional information:
 1) To login, access the endpoint with 'x-www-form-urlencoded' body with keys 'username' and 'password'.
