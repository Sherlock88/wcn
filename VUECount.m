dm=1000
dp=300;
a=50;
b=50;
Nall=10000;
C=3
total_area=pi*(dp+a)^2-pi*(dp-b)^2
non_harea=pi*(dm^2)-C*pi*(dp^2);
inner_area=pi*(dp-b)^2;
outer_area=pi*(dp+a)^2;
pico_area=pi*dp^2;
fun1 = @(A) (((1/C)*(2/3*Nall)*((A-pico_area)/A))+((1/3)*Nall*((outer_area-A)/(non_harea))))/total_area;
fun2 = @(A) (A.^0)*((1/3).*Nall.*((outer_area-pico_area)/(non_harea)))/total_area;
MVUE=integral(fun1,pico_area,outer_area)+integral(fun2,inner_area,pico_area);
fun3 = @(A) (((1/C)*(2/3*Nall)*((A-inner_area)/A))+((1/3)*Nall*((pico_area-A)/(non_harea))))/total_area;
fun4 = @(A) (A.^0)*((1/3)*Nall*((pico_area-inner_area)/(non_harea)))/total_area;
PVUE=integral(fun3,inner_area,pico_area)+integral(fun4,pico_area,outer_area);
MVUE
PVUE
total_MVUE=MVUE*C
total_PVUE=PVUE*C