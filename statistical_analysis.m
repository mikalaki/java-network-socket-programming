%% Statistical Analysis for the Computer Networks 2 project
%  Java Socket Programming data statistical analysis.


%% Importing Data
echo_responeTimes_delay= importdata('echoPacketsResTimes_delay.csv');
echo_responeTimes_noDelay= importdata('echoPacketsResTimes_NoDelay.csv');

%% Part [B] of the assigment 
% Get the Throughput for each second of echo packets interchange process.
ThroughputWithDelay = getThroughputPerSec(echo_responeTimes_delay);
ThroughputNoDelay = getThroughputPerSec(echo_responeTimes_noDelay);

%%%%%% meta theelei MA filtro kai eimaste ok. ta bytes enos mynhmatos einai
%%%%%% 32

% R1 diagram.
