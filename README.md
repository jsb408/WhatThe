# 이게 뭐여?!

어르신을 위한 스마트 도우미 음성채팅 앱 

- 진행기간 : 2020. 12. 06 ~ 2020. 12. 20
- 사용기술 : Android Studio, Kotlin, Firebase, MediaProjection, MediaRecorder, MediaPlayer, RemoteMonster, Glide

<p align="center"><img src="https://user-images.githubusercontent.com/55052074/116388312-a8592380-a856-11eb-906c-37425c9a0bd7.jpg" width="30%"/> <img src="https://user-images.githubusercontent.com/55052074/116388318-abecaa80-a856-11eb-9d15-0958a252ba87.jpg" width="30%"/></p>


## 서비스 소개

- 이게 뭐여?!는 스마트폰 사용이 어려운 어르신들을 위해 젊은 사람들의 도움을 받을 수 있도록 연결해주는 플랫폼입니다.
- 키보드의 사용과 버튼의 개수를 최소화하고 단일 버튼과 음성만으로 도움을 요청할 수 있습니다.
- 도움이 필요한 회원은 별도의 가입이나 로그인 없이 도움받기 버튼을 누르고 도움이 필요한 내용을 녹음 후 업로드하면 등록이 가능합니다.
- 도움을 주고싶은 회원은 회원가입 및 로그인 후 등록 된 도움 요청 글의 내용을 듣고 선택해 도움이 필요한 회원과 연결할 수 있습니다.
- 두 회원이 연결되면 통화가 시작되어 음성을 통해 소통할 수 있습니다.
- 도움이 필요한 사람에게는 플로팅 버튼이 표시되어 클릭 한 번으로 도움을 주는 사람에게 현재 화면을 캡쳐하여 전송할 수 있습니다.

## 상세 기능 소개

### 1. 도움받기

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116388732-1998d680-a857-11eb-9444-765f89626e68.png"/></p>

- 도움이 필요하다면 별도의 로그인 과정 없이 도움 요청이 가능합니다.
- 메인 화면의 **도움받기** 버튼 클릭 시 도움받기 모드로 진입합니다.

### 1-1. 도움받기

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116388795-2f0e0080-a857-11eb-9b86-94f9a8899431.png"/></p>

- 같은 위치의 버튼을 눌러 녹음을 시작할 수 있습니다.

### 1-2. 녹음하기

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116388830-38976880-a857-11eb-81a1-6a1a015393e5.png"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116388835-3a612c00-a857-11eb-8c86-a90e250fbeee.png"/></p>

- 녹음이 시작되면 **녹음완료** 버튼을 눌러 녹음을 끝낼 수 있습니다.
- 중앙 버튼을 눌러 녹음된 결과물을 다시 들어볼 수 있습니다.
- 다시 녹음 버튼을 누르면 한 단계 전으로 가 다시 녹음을 시작할 수 있습니다.
- 요청하기 버튼을 누르면 녹음 파일이 서버에 업로드되고 대기 화면으로 넘어갑니다.

### 1-3. 요청 대기

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116388905-4fd65600-a857-11eb-90c7-ff4b5d3b844c.png"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116388911-51a01980-a857-11eb-8974-0930b52a4d44.png"/></p>

- 대기 화면에서는 현재 접속중인 도우미들의 인원 수를 확인할 수 있습니다.
- 도우미가 해당 도움 글을 선택해 연결할 경우 메시지가 표시되고 3초 후 음성채팅이 시작됩니다.

### 2. 도움주기

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116389072-81e7b800-a857-11eb-966c-2205e79868d5.png"/></p>

- 도움을 주고싶다면 **도움주기** 버튼을 눌러 도움주기로 입장할 수 있습니다.
- 도움을 주기 위해서는 회원가입 및 로그인을 진행해야합니다.

### 2-1. 회원가입

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116389271-b78ca100-a857-11eb-83c8-f6d4ee7abdf6.png"/></p>

- **회원가입** 버튼을 통해 회원가입 화면에 진입할 수 있습니다.
- 별도의 인증과정은 없으며 양식에 맞게 작성 후 가입이 가능합니다.

### 2-2. 도움 목록 조회

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116389375-d2f7ac00-a857-11eb-987c-69c048c7a5b0.png"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116389381-d428d900-a857-11eb-86fb-07826dc91497.png"/></p>

- 생성되어있는 계정이 있다면 로그인을 통해 도움이 필요한 도움들의 목록을 볼 수 있습니다.
- 죄측의 재생버튼을 터치하면 녹음된 음성이 재생되며 어떤 도움이 필요한지 들을 수 있습니다.

### 2-3. 도움주기

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116389492-f3c00180-a857-11eb-9522-62c6796c892a.png"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116389501-f7538880-a857-11eb-9e5e-507796d34c84.png"/></p>

- 도움을 주고싶은 도움글을 터치하면 다이얼로그가 표시됩니다.
- 확인 버튼을 누르면 해당 도움 게시자와 연결되고 음성채팅방으로 입장합니다.

### 3. 도움제공

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116389623-1eaa5580-a858-11eb-9419-d9dda395e2f9.png"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116389640-236f0980-a858-11eb-996a-bab66ba855e2.png"/></p>

- 도움은 RemoteMonster의 음성채팅 기능과 Firestore를 활용한 실시간 채팅을 사용합니다.
- 키보드를 사용하지 않고 오로지 음성과 화면 전송으로만 진행합니다.

### 3-1. 화면 전송

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116389746-47cae600-a858-11eb-9dc3-8db816d65346.png"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116389753-4994a980-a858-11eb-8bfc-d5439acdb60c.png"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116389761-4bf70380-a858-11eb-9a0b-fab244e46a42.png"/></p>

- 도움을 요청한 회원에게는 화면 녹화 권한 허용 다이얼로그가 표시됩니다.
- 권한을 허용하면 플로팅 버튼 위에 미러링 화면이 표시되고 앱을 닫아도 플로팅뷰 위에 스마트폰의 화면이 표시됩니다.

### 3-2. 화면 캡처

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116389863-6af59580-a858-11eb-941a-b54449bf6ec6.png"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116389888-6e891c80-a858-11eb-8c91-d26ed78b9262.png"/></p>

- 도움을 요청한 회원이 플로팅 버튼을 터치하면 해당 화면이 캡처되어 채팅방에 전송됩니다.
- 도움을 주는 회원은 해당 화면을 보고 음성으로 도움을 줄 수 있습니다.

## 보완 사항

- 플로팅 버튼 모양 변경
- 캡처 이미지 화질 개선
