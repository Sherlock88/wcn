delimiter = ' ';
formatSpec = '%f%f%[^\n\r]';

filename = 'E:\Experiment\Code\Projects\WCN\Data\MACRO-Dist-600-2000-1';
fileID = fopen(filename,'r');
dataArray = textscan(fileID, formatSpec, 'Delimiter', delimiter, 'MultipleDelimsAsOne', true,  'ReturnOnError', false);
fclose(fileID);
MC_X = dataArray{:, 1};
MC_Y = dataArray{:, 2};

filename = 'E:\Experiment\Code\Projects\WCN\Data\PICO-Dist-600-6-1';
fileID = fopen(filename,'r');
dataArray = textscan(fileID, formatSpec, 'Delimiter', delimiter, 'MultipleDelimsAsOne', true,  'ReturnOnError', false);
fclose(fileID);
PC_X = dataArray{:, 1};
PC_Y = dataArray{:, 2};

filename = 'E:\Experiment\Code\Projects\WCN\Data\UE-Dist-600-2000-1';
fileID = fopen(filename,'r');
dataArray = textscan(fileID, formatSpec, 'Delimiter', delimiter, 'MultipleDelimsAsOne', true,  'ReturnOnError', false);
fclose(fileID);
UE_X = dataArray{:, 1};
UE_Y = dataArray{:, 2};

xlabel('X Coordinate');
ylabel('Y Coordinate');
title('Locations of Macrocells and Picocells an UEs');
plot(MC_X, MC_Y, 'rh', PC_X, PC_Y, 'bo', UE_X, UE_Y, 'g*');
%plot(MC_X, MC_Y, 'rh', PC_X, PC_Y, 'bo');