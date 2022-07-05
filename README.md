# 몰입캠프 Week1 Project
## 프로젝트 개요
3개의 탭이 들어가는 안드로이드 앱.
각각 연락처, 갤러리, To-do 리스트 기능을 제공한다.



## 앱 설명
### 앱 초기 실행
> 최초로 앱을 실행시키면 연락처, 전화, 파일에 대한 접근권한을 요청한다. 권한을 수락하지 않을 시 앱을 사용할 수 없으니 필히 수락하도록 하자.  
> 
> <img src ="https://user-images.githubusercontent.com/67325264/177319821-3f172acd-e745-41fe-9025-48f2d917ff1d.jpg" height="300"/>
### Tab1: Contacts
>
> Tab1의 contacts에서는 휴대전화의 연락처를 불러와 선택한 연락처를 저장할 수 있다. 삭제 버튼을 이용해 연락처를 선택해 삭제할 수 있다. 기본 레이아웃인 이름을 클릭하면 해당 연락처의 상세정보, 전화 및 문자 버튼이 노출되며 버튼을 누르면 해당 번호로 전화와 문자전송이 가능하다.  
> 
> <img src = "https://user-images.githubusercontent.com/67325264/177312210-c2644eb1-270e-433f-959e-bc63cd35385b.gif" height="300"/> <img src ="https://user-images.githubusercontent.com/67325264/177314318-5c078217-f17e-4b69-ba39-55e570c198c3.gif" height="300"/> <img src ="https://user-images.githubusercontent.com/67325264/177313231-6e44ce4b-0f1b-4f00-894f-8af7bc544fca.gif" height="300"/> <img src ="https://user-images.githubusercontent.com/67325264/177320092-806113ee-2751-4b67-bb64-dbd701d3f078.jpg" height="300"/>  
> 
>  연락처 추가   연락처 삭제   전화걸기, 문자전송 연락처 검색&상세정보


### Tab2: Gallery
> Tab2의 갤러리에서는 Tab1 연락처와 같이 휴대전화 갤러리로부터 추가 및 삭제가 가능하다. 각 3개의 열로 배열되며 원하는 사진을 모아 볼 수 있다. 휴대전화 갤러리에서 삭제되더라도 어플리케이션에 저장된 이미지는 보존된다.  
>  
> <img src = "https://user-images.githubusercontent.com/67325264/177320427-aa82602a-5b8d-486c-8580-e0ca63e42a40.jpg" height="300"/> <img src = "https://user-images.githubusercontent.com/67325264/177320493-db8147cb-2078-4842-a70d-02efe259f06e.jpg" height="300"/>  
> 
>   사진 추가     사진 삭제   

### Tab3: To-do list
> 투두리스트에는 원하는 날짜에 원하는 일정을 추가할 수 있다. Calendar는 week view와 month view로 축소 및 확대가 가능하다. 날짜를 선택하면 그 날의 할 일을 확인해 볼 수 있으며, 할일이 저장된 날짜에는 dot으로 표시가 된다. 할일의 상태는 엑스, 체크, 세모로 각각 진행중, 완료, 보류로 표시할 수 있다. 어떤 날짜에 저장된 모든 할 일이 완료상태가 되면 해당 날짜의 dot 표기는 사라진다. 모두 보류 표시될 경우 dot는 회색으로 표시된다. 할일을 잘 확인하고 수행해서 생산적인 사람이 되도록 하자.  
> 
> <img src = "https://user-images.githubusercontent.com/67325264/177316143-a097d1a0-d3e4-4037-8caa-941762af833b.gif" height="300"/> <img src = "https://user-images.githubusercontent.com/67325264/177316655-1e3fe915-909e-4ebc-a270-b91a5d325437.gif" height="300"/> <img src = "https://user-images.githubusercontent.com/67325264/177317092-91056966-f0ac-4598-b2e2-6929fd694d92.gif" height="300"/> <img src = "https://user-images.githubusercontent.com/67325264/177317679-afb63b6b-46b4-455a-88f8-b2cce56b00ec.gif" height="300"/>  
> 
>  Calender 확대    할일 추가    상태 표시     할일 삭제

### 구현
3개의 탭이 공통적으로 Recycler View를 사용해 항목을 보여주고 항목 추가와 삭제 기능을 제공한다. 데이터 유형별로 Adapter를 각각 구현하고 적절한 필터를 추가하여 상황에 맞는 항목만 표시되도록 했다. 데이터의 추가와 삭제를 담당하는 Floating Action Button을 만들고 3개의 탭에서 공통적으로 사용하여 사용성을 높였다.
#### Floating Action Button
* 버튼을 눌렀을 때 발생하는 애니메이션을 각각 작성하고 on-click listener에서 애니메이션을 시작하도록 했다.
#### Tab1: Contacts
*  내부 저장소에 json 형식으로 연락처 목록을 저장하고 불러온다.
* On-click listner에서 View의 visibility를 변경하도록 해서 누르면 펼쳐지는 연락처 항목을 만들었다.


#### Tab2: Gallery
* Recycler View에서 Grid Layout Manager를 이용해 한 행에 3개의 이미지가 표시되도록 했다.
#### Tab3: To-do list
* 앱 내부 저장소에 json 형식으로 to-do 목록을 저장하고 불러온다.
* Material Calander View의 Decorator를 이용하여 할 일이 남아 있는 날짜에 점이 표시되도록 했다.
#### 개선할 점
* 삭제 버튼을 눌렀을 때 사용자의 의사를 재차 확인하는 대화 상자를 표시한다.
* 갤러리의 사진을 터치할 때 사진을 크게 볼 수 있는 기능을 추가한다.
* 데이터가 로드되는 시점을 변경하여 탭을 눌렀을 때 지연되는 시간을 줄인다.
* 연락처와 to-do 리스트 탭에 수정 기능을 추가한다.

#### Contributors
> Kim Taeyun [github page](https://https://github.com/tykim5931) 
> Lee Changhae  [github page](https://github.com/chlee973) 

