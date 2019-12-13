require_relative 'grid/distance_grid'
require_relative 'algos/sidewinder_mine'
require_relative 'seed'

seed = Seed.set(ENV['SEED'])
puts "Seed: #{seed}"

grid = DistanceGrid.new(4, 4)

SidewinderMine.on(grid, ENV['DEBUG'])

start = grid[0, 0]

distances = start.distances

grid.distances = distances

puts "Complete."
puts grid