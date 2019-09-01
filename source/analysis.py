import matplotlib.pyplot as plt
import csv

data = ["chaotic", "onlyrsu", "rsuandobu"]
kpi_coll = {}
kpi_near_coll = {}
for d in data:
    kpi_coll[d] = {}
    kpi_near_coll[d] = {}
    with open('../SARLproject/formalproject/data/' + d + '.csv' ,'r') as csvfile:
        plots = csv.reader(csvfile, delimiter=',')
        for row in plots:
            kpi_coll[d][float(row[0])] = int(row[1])
            kpi_near_coll[d][float(row[0])] = int(row[2])

#print(kpi_coll)
#print(kpi_near_coll)

f1 = plt.figure(1)

plt.plot(list(kpi_coll['chaotic'].keys()), list(kpi_coll['chaotic'].values()), color='red', label='Chaotic World')
plt.plot(list(kpi_coll['onlyrsu'].keys()), list(kpi_coll['onlyrsu'].values()), color='orange', label='Only RSU')
plt.plot(list(kpi_coll['rsuandobu'].keys()), list(kpi_coll['rsuandobu'].values()), color='green', label='Warning System')

plt.xlabel('Time')
plt.ylabel('Number of collisions')
plt.title('KPI: Collisions')
plt.legend()

f2 = plt.figure(2)

plt.plot(list(kpi_near_coll['chaotic'].keys()), list(kpi_near_coll['chaotic'].values()), color='red', label='Chaotic World')
plt.plot(list(kpi_near_coll['onlyrsu'].keys()), list(kpi_near_coll['onlyrsu'].values()), color='orange', label='Only RSU')
plt.plot(list(kpi_near_coll['rsuandobu'].keys()), list(kpi_near_coll['rsuandobu'].values()), color='green', label='Warning System')

plt.xlabel('Time')
plt.ylabel('Number of near collisions')
plt.title('KPI: Near Collisions')
plt.legend()

plt.show()