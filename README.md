# BraPolar
 BraPolar, an m-Health for remote monitoring of patients with Bipolar Affective Disorder, presenting real-time mood and behavior fluctuations in participants through interaction with their mobile devices. 
 
**Specialist user (center) monitoring Mood fluctuations of two patients (left and right)**
<h1 align="center">
  <img 
    src="./doc/demo.gif"
  />
</h1>

---
## 🧾 About
**BraPolar v1.0**, is an Android m-Health for remote monitoring of patients with Bipolar Affective Disorder. The app present in real-time mood and behavior fluctuations to specialists from participants through their interaction with the mobile devices. This project was part of my master degree dissertation in Brazil. In this work, I could execute all software development cycles from scratch, application and documentation. Also, I could practice all research process of scientific research writing and presenting this work in a international conference at Kyoto, Japan.

---
## 📷 Screenshots


**Project scratch**
<h1>
  <img 
    src="./doc/image033.jpg"
  />
</h1>


**Task planning**
<h1>
  <img 
    src="./doc/maquetado_Trello.JPG"
  />
</h1>


**Bipolar sympthons and how follow-up with cellphone sensors**
<h1>
  <img 
    src="./doc/image01.jpg"
  />
</h1>



**Login arquitecture**
<h1>
  <img 
    src="./doc/image02.png"
  />
</h1>



**BraPolar login (left), participant home (center), keyboard typing (left)*
<h1>
  <img 
    src="./doc/brapolar_Login.png"
  />
  <img 
    src="./doc/brapolar_participanteHome.jpg"
  />
  <img 
    src="./doc/brapolar_KeyboardTestWhatsapp.jpg"
  />
</h1>

**Firebase active users during data capture**
<h1>
  <img 
    src="./doc/usuarios_ativos.JPG"
  />
</h1>


**Firebase non-relational database instance**
<h1>
  <img 
    src="./doc/firebasedatabase.JPG"
  />
</h1>


**Work presentation **
<h1>
  <img 
    src="./doc/workflow.jpg"
  />
</h1>

---
## 🕖 Versioning
- BraPolar v3.0 (expected in 20/06/2023)
- BraPolar v2.4 (relased in 07/07/2022)
- BraPolar v2.3 (relased in 21/05/2022)
- BraPolar v2.2 (relased in 02/08/2021)
- BraPolar v2.1 (relased in 17/11/2020)
- BraPolar v2.0 (relased in 19/08/2019)
- BraPolar v1.0 (relased in 11/02/2019)
- BraPolar v0.5 (relased in 12/12/2018)

This project is part of my PhD thesis, which is running. From v1.0, to preserve scientific code rights, I will not upload new public versions on Github until concluding my doctoral degree and get the authorization to publish all code.    

---
## ✅ Main features
**Patient**
- [x] Set mood
- [x] 

**Specialist**


---
## 🔧 Technology
- [Android](https://httpd.apache.org/) 💚
- [Java](https://www.python.org/) 
- [Firebase](https://www.djangoproject.com/) 

---
## 👨‍💻 How to Setup

```bash
  # Clone the project
  $ git clone https://github.com/abelgonzalez/Ajustes.git
```
```bash
  # Enter directory
  $ cd Ajustes
```

Download and install:

 - [XAMPP for Windows 7.4.29](https://www.apachefriends.org/download.html)
  
 - [Python 3.7.7](https://www.python.org/downloads/release/python-377/)

 - [Django 1.7](https://www.djangoproject.com/download/1.7/tarball/) 
   or
  ```bash
    # Clone the project
    $ pip install Django==1.7
  ```
  
- [Visual Studio Code 1.67.2](https://code.visualstudio.com/Download)

- [Microsoft C++ Build Tools](https://visualstudio.microsoft.com/visual-cpp-build-tools/)

- On your system you would do this by running this in your command prompt:
```bash     
    $ set "MOD_WSGI_APACHE_ROOTDIR=C:\xampp\apache"
  ```
-  Install mod_wsgi 4.9.2
   ```bash
    # Install the last version
    $ pip install mod_wsgi
   ```
  

- You can add WSGIScriptAlias /mysite "C:/xampp/htdocs/wsgi/scripts/mysite.wsgi" in wsgi.conf to run http://YOURSITE/mysite, or you can just run http://YOURSITE/wsgi/mysite.wsgi
Relaunch apache if necessary.
  
- At the end of file C:\xampp\apache\conf\httpd.conf , add the following:
  AddHandler cgi-script .py
  ScriptInterpreterSource Registry-Strict
- Locate <IfModule dir_module> label and add **index.py** at right of **home.htm**
- Save the httpd.conf changes
  
- Open MySQL and import the database file **ajustes_bd.sql** in the project source.
  Database name: ajustes_UM
  utf: utf8_spanish_ci
  
  
 - Go to project root folder (**tesis**)
  ```bash
    # Run
    $ python manage.py migrate
    $ python manage.py makemigrations
    $ python manage.py migrate
  ```
     
---
## 😎 How to Run

 - Make sure the database was correctly loaded and MySQL is running.
  
 - In root folder (**tesis**) run:
  ```bash
    # Run
    $ python manage.py runserver 
  ```

---
## 👉 Additional information
* App Demo https://youtu.be/4jX4oDhjTtc
* Technical and User's Manual is available in [Doc](https://github.com/abelgonzalez/Ajustes/tree/main/doc) folder (in Spanish).
* In case of sensitive bugs like security vulnerabilities, don't hesitate to contact me at abelgodev@gmail.com instead of using the issue tracker. I value your effort to improve the security and privacy of this project!

---
## 📝 License

This project is under the MIT license. See the file <a href="https://github.com/abelgonzalez/BraPolar/LICENSE">LICENCE</a> for more details.

---
## 🧑‍💻 Author
<p align="center">Done with 💙 by Abel González Mondéjar</p>


[![LinkedIn Badge](https://img.shields.io/badge/-Abel_González_Mondéjar-blue?style=flat-square&logo=Linkedin&logoColor=white&link=https://www.linkedin.com/in/abelgonzalezmondejar/)](https://www.linkedin.com/in/abelgonzalezmondejar/)

